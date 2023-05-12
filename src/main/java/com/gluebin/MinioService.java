package com.gluebin;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;

@Service
public class MinioService {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;

    public void uploadFile(String pasteText, String path) throws ServerException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, XmlParserException, InsufficientDataException, InternalException, InvalidResponseException {
        byte[] pasteBytes = pasteText.getBytes(StandardCharsets.UTF_8);
        InputStream pasteIs = new ByteArrayInputStream(pasteBytes);
        minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(path).stream(pasteIs, pasteIs.available(), -1).contentType("text/plain").build());
    }
}
