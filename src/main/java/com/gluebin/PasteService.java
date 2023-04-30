package com.gluebin;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PasteService {
	@Autowired private PasteRepository repo;

	public Paste add(Paste paste) {
		return repo.save(paste);
	}

	public Paste update(Paste paste) throws PasteNotFoundException {
		if (!repo.existsById(paste.getShortLink())) {
			throw new PasteNotFoundException();
		}

		return repo.save(paste);
	}

	public Paste get(String shortLink) throws PasteNotFoundException {
		Optional<Paste> result = repo.findById(shortLink);

		if (result.isPresent()) {
			return result.get();
		}

		throw new PasteNotFoundException();
	}

	public List<Paste> list() {
		return (List<Paste>) repo.findAll();
	}

	public void delete(String id) throws PasteNotFoundException {
		if (repo.existsById(id)) {
			repo.deleteById(id);
		}

		throw new PasteNotFoundException();
	}
}
