package com.gpw.radar.domain.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gpw.radar.domain.User;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Entity
@Table(name = "CHAT_MESSAGE")
@Immutable
public class ChatMessage {

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

	@NotNull
	@Size(min = 1, max = 512)
	@Column(length = 512, nullable = false)
	private String message;

    @Transient
    private String link;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
