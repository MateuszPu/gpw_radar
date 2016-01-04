package com.gpw.radar.domain.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "USER_MESSAGE")
public class UserMessage extends ChatMessage {

	@NotNull
	@Size(min = 1, max = 128)
	@Column(length = 128, nullable = false)
	@JsonIgnore
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String getChatMessage() {
		return message;
	}
}
