package com.gluebin;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/pastes")
public class PastesApiController {
    private static final Logger logger = LoggerFactory.getLogger(PastesApiController.class);
    private PasteService service;
    @Autowired
    MinioService minioService;

    protected PastesApiController(PasteService service, ModelMapper mapper) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody Paste paste, HttpServletRequest request) {
        // removed @Valid from infront of paste to disable validation
        // logic for creating short link from
        // https://github.com/donnemartin/system-design-primer/tree/master/solutions/system_design/pastebin
        String ip = request.getRemoteAddr();
        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
        paste.setCreatedAt(timeStamp);
        logger.debug("pasteText {}", paste.getPasteText());
        String shortUrl = getShortUrl(ip + timeStamp.toString()).get();
        paste.setShortLink(shortUrl);
        paste.setPastePath(shortUrl);
        paste.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        paste.setExpirationLengthInMinutes(10);
        try {
            minioService.uploadFile(paste.getPasteText(), paste.getPastePath());
        } catch (InvalidKeyException | ServerException | ErrorResponseException | NoSuchAlgorithmException
                | XmlParserException | InsufficientDataException | InternalException | InvalidResponseException
                | IOException e) {
           logger.debug("{}", e);
        }
        service.add(paste);
        URI uri = URI.create("/pastes/" + paste.getShortLink());
        return ResponseEntity.created(uri).body(paste);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") String id) {
        try {
            Paste paste = service.get(id);
            paste.setPasteText(minioService.getFile(paste.getPastePath()));
            return ResponseEntity.ok(paste);
        } catch (InvalidKeyException | ErrorResponseException | InsufficientDataException | InternalException
                | InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException
                | IllegalArgumentException | IOException | PasteNotFoundException e) {
            logger.debug("{}", e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * gets the first 7 characters of the sha256 sum
     * of a url
     */
    private Optional<String> getShortUrl(String s) {
        String base62First7 = null;
        try {
            // Create an instance of MessageDigest with SHA-256 algorithm
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // Convert the input string to bytes
            byte[] encodedHash = digest.digest(s.getBytes());
            base62First7 = encodeBase62(encodedHash);
            return Optional.of(base62First7);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private String encodeBase62(byte[] bytes) {
        BigInteger number = new BigInteger(1, bytes);
        StringBuilder sb = new StringBuilder();
        while (number.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] quotientAndRemainder = number.divideAndRemainder(BigInteger.valueOf(62));
            BigInteger quotient = quotientAndRemainder[0];
            BigInteger remainder = quotientAndRemainder[1];

            sb.insert(0, BASE62_CHARS.charAt(remainder.intValue()));
            number = quotient;
        }

        return sb.toString().substring(0, 7);
    }
}
