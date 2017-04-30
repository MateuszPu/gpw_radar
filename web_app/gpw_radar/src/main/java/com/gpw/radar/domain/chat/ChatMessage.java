package com.gpw.radar.domain.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gpw.radar.domain.User;
import com.gpw.radar.elasticsearch.newsmessage.NewsMessage;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Entity
@Table(name = "CHAT_MESSAGE")
@Immutable
public class ChatMessage {

    @Id
    @GenericGenerator(name = "seq_id", strategy = "com.gpw.radar.domain.generator.StringIdGenerator")
    @GeneratedValue(generator = "seq_id")
    private String id;

    @ManyToOne
    @NotNull
    @JsonIgnore
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_user"), nullable = false)
    private User user;

    @NotNull
    @Column(name = "created_date", nullable = false)
    private ZonedDateTime createdDate = ZonedDateTime.now();

    @NotNull
    @Column(name = "news_date_time", nullable = false)
    private LocalDateTime newsDateTime;

    @NotNull
    @Size(min = 1, max = 512)
    @Column(length = 512, nullable = false)
    private String message;

    @Transient
    private String link;

    public ChatMessage() {
    }

    public ChatMessage(NewsMessage newsMessage) {
        this.link = newsMessage.getLink();
        this.newsDateTime = newsMessage.getNewsDateTime();
        this.message = newsMessage.transformToChatMessageContent();
    }

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

    public LocalDateTime getNewsDateTime() {
        return newsDateTime;
    }

    public void setNewsDateTime(LocalDateTime newsDateTime) {
        this.newsDateTime = newsDateTime;
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
