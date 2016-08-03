package com.rss.parser.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class GpwNews {

    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private final ZonedDateTime timeNews;
    private final String message;
    private final String link;

    public GpwNews(ZonedDateTime timeNews, String message, String link) {
        this.timeNews = timeNews;
        this.message = message;
        this.link = link;
    }

    public ZonedDateTime getTimeNews() {
        return timeNews;
    }

    public String getMessage() {
        return message;
    }

    public String getLink() {
        return link;
    }
}
