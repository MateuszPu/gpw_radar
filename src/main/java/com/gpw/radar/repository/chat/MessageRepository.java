package com.gpw.radar.repository.chat;

import com.gpw.radar.domain.chat.UserMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<UserMessage, Long> {

}
