package com.gluebin;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity @Table(name = "pastes")
public class Paste{

	@Id
    private String shortLink;

	@Column(nullable = false)
	private long expirationLengthInMinutes;

	//@Column(nullable = false)
	private Timestamp createdAt;
	
	@Column(length = 255)
    //@NotBlank(message = "Paste path must not be empty")
	private String pastePath;

	@Transient
	private String pasteText;

	public void setPasteText(String pasteText) {
		this.pasteText = pasteText;
	}

	public String getPasteText() {
		return pasteText;
	}

	public String getShortLink() {
		return shortLink;
	}

	public void setShortLink(String shortLink) {
		this.shortLink = shortLink;
	}

	public long getExpirationLengthInMinutes() {
		return expirationLengthInMinutes;
	}

	public void setExpirationLengthInMinutes(long expirationLengthInMinutes) {
		this.expirationLengthInMinutes = expirationLengthInMinutes;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public String getPastePath() {
		return pastePath;
	}

	public void setPastePath(String pastePath) {
		this.pastePath = pastePath;
	}

}
