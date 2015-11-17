package com.gpw.radar.domain.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gpw.radar.domain.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Entity
@Inheritance( strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class ChatMessage {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private long id;

	@ManyToOne
	@NotNull
	@JsonIgnore
    @JoinColumn(name="user_id", foreignKey = @ForeignKey(name="FK_user"), nullable = false)
	private User user;

	@NotNull
	@Column(name = "user_login", nullable = false)
	private String userLogin;

	@NotNull
    @Column(name = "created_date", nullable = false)
    private ZonedDateTime createdDate = ZonedDateTime.now();

	public abstract String getChatMessage();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}

	public ZonedDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(ZonedDateTime createdDate) {
		this.createdDate = createdDate;
	}

}
