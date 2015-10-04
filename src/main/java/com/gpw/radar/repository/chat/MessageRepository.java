package com.gpw.radar.repository.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gpw.radar.domain.chat.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

}
