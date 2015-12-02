package com.gpw.radar.service.chat;

import com.gpw.radar.domain.User;
import com.gpw.radar.domain.chat.ChatMessage;
import com.gpw.radar.domain.chat.UserMessage;
import com.gpw.radar.repository.UserRepository;
import com.gpw.radar.repository.chat.MessageRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.security.Principal;

@Service
public class MessageService {

    @Inject
    private MessageRepository messageRepository;

    @Inject
    private UserRepository userRepository;

    public ChatMessage createUserMessage(String message, Principal principal) {
        if (message.length() > 128 || message.length() < 1) {
            return null;
        }
        String userLogin = principal.getName();
        User currentUser = userRepository.findOneByLogin(userLogin).get();
        UserMessage msg = new UserMessage();
        msg.setMessage(message);
        msg.setUser(currentUser);
        msg.setUserLogin(userLogin);
        messageRepository.save(msg);
        return msg;
    }
}
