package com.gpw.radar.domain.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gpw.radar.domain.User;
import com.gpw.radar.service.util.RandomUtil;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Entity
@Inheritance( strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class ChatMessage {

    @Id
    @GenericGenerator(name="seq_id", strategy="com.gpw.radar.domain.generator.StringIdGenerator")
    @GeneratedValue(generator="seq_id")
    private String id;

	@ManyToOne
	@NotNull
	@JsonIgnore
    @JoinColumn(name="user_id", foreignKey = @ForeignKey(name="FK_user"), nullable = false)
	private User user;

	@NotNull
    @Column(name = "created_date", nullable = false)
    private ZonedDateTime createdDate = ZonedDateTime.now();

	public abstract String getChatMessage();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ZonedDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(ZonedDateTime createdDate) {
		this.createdDate = createdDate;
	}

}
