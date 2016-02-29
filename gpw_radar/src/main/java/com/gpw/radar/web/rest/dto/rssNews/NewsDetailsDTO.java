package com.gpw.radar.web.rest.dto.rssNews;

import java.time.ZonedDateTime;

public class NewsDetailsDTO {

    private String message;
    private String link;
    private ZonedDateTime createdDate;

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

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(ZonedDateTime createdDate) {
        this.createdDate = createdDate;
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
