package com.gpw.radar.web.rest.dto.rssNews;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

public class NewsDetailsDTO {

    private String message;
    private String link;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime newsDateTime;

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

    public LocalDateTime getNewsDateTime() {
        return newsDateTime;
    }

    public void setNewsDateTime(LocalDateTime newsDateTime) {
        this.newsDateTime = newsDateTime;
    }

    public String getChatMessage() {
        StringBuilder str = new StringBuilder();
        str.append("<a href=\"");
        str.append(getLink());
        str.append("\" target=\"_blank\">");
        str.append(getMessage());
        str.append("</a> ");
        return str.toString();
    }
}
