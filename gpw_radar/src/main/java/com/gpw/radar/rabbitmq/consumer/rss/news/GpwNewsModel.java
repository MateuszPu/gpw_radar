package com.gpw.radar.rabbitmq.consumer.rss.news;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.time.LocalDateTime;

public class GpwNewsModel {

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime newsDateTime;
    private String message;
    private String link;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime getTimeNews() {
        return newsDateTime;
    }

    public void setTimeNews(LocalDateTime newsDateTime) {
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
