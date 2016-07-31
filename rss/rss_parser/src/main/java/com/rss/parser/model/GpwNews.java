package com.rss.parser.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

public class GpwNews {

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private final LocalDateTime timeNews;
    private final String message;
    private final String link;

    public GpwNews(LocalDateTime timeNews, String message, String link) {
        this.timeNews = timeNews;
        this.message = message;
        this.link = link;
    }

    public LocalDateTime getTimeNews() {
        return timeNews;
    }

    public String getMessage() {
        return message;
    }

    public String getLink() {
        return link;
    }
}
