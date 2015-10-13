package com.gpw.radar.repository.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gpw.radar.domain.chat.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

}
