package com.gpw.radar.service.chat;

import com.gpw.radar.domain.chat.ChatMessage;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ChatMessageLoader {
    ResponseEntity<List<ChatMessage>> getLastMessages(int page);
    ResponseEntity<List<ChatMessage>> getOlderMessages(int page);
}
