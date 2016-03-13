package com.gpw.radar.service.chat;

import com.gpw.radar.web.rest.dto.chat.ChatMessageDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ChatMessageLoader {
    ResponseEntity<List<ChatMessageDTO>> getLastMessages(int page);
    ResponseEntity<List<ChatMessageDTO>> getOlderMessages(int page);
}
