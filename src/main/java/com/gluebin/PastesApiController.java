package com.gluebin;

import java.net.URI;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/pastes")
public class PastesApiController {
	private PasteService service;

	protected PastesApiController(PasteService service, ModelMapper mapper) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<?> add(@RequestBody @Valid Paste paste) {
		service.add(paste);

		URI uri = URI.create("/pastes/" + paste.getShortLink());

		return ResponseEntity.created(uri).body(paste);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> get(@PathVariable("id") String id) {
		try {
			Paste paste = service.get(id);
			return ResponseEntity.ok(paste);

		} catch (PasteNotFoundException e) {
			e.printStackTrace();
			return ResponseEntity.notFound().build();
		}
	}

}
