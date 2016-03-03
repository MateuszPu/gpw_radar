package com.gpw.radar.web.rest.dto.chat;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public class ChatMessageDTO {

    private String chatMessage;
    private String userLogin;
    private ZonedDateTime createdDate;

    public String getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
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
