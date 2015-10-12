package com.gpw.radar.repository.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gpw.radar.domain.chat.UserMessage;

public interface MessageRepository extends JpaRepository<UserMessage, Long> {

}
