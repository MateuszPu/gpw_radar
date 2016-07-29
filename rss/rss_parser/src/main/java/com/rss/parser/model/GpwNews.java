package com.rss.parser.model;

import java.time.LocalDateTime;

/**
 * Created by mateusz on 28.07.2016.
 */
public class GpwNews {

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
