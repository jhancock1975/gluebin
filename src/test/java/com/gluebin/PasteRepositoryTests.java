package com.gluebin;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class PasteRepositoryTests {

	@Autowired private PasteRepository repo;

	@Test
	public void testAddSuccess() {
		Paste newPaste = new Paste();
        newPaste.setShortLink("test");
        newPaste.setExpirationLengthInMinutes(10000);
        newPaste.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        newPaste.setPastePath("/test/pasth");
		Paste persistedPaste = repo.save(newPaste);

		assertThat(persistedPaste).isNotNull();
        repo.delete(newPaste);
	}
}
