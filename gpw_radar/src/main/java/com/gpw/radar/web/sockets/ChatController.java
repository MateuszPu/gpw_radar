package com.gpw.radar.web.sockets;

import com.gpw.radar.domain.chat.ChatMessage;
import com.gpw.radar.service.chat.ChatMessageService;
import com.gpw.radar.service.mapper.ChatMessageMapper;
import com.gpw.radar.web.rest.dto.chat.ChatMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

@Controller
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatMessageMapper chatMessageMapper;
    private Set<String> users = new HashSet<String>();

    @Autowired
    public ChatController(SimpMessageSendingOperations messagingTemplate, ChatMessageService chatMessageService,
                          ChatMessageMapper chatMessageMapper) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageService = chatMessageService;
        this.chatMessageMapper = chatMessageMapper;
    }

    @SubscribeMapping("/webchat/send/message")
    @SendTo("/webchat/recive")
    public ChatMessageDTO sendChatMessage(Message message, Principal principal) {
        ChatMessage msg = chatMessageService.createUserMessage(message.getMessage(), principal);
        return chatMessageMapper.mapToDto(msg);
    }

    @SubscribeMapping("/webchat/user/login")
    public void userLogin(Principal principal) throws InterruptedException {
        users.add(principal.getName());
        usersCount();
        messagingTemplate.convertAndSend("/webchat/user", users);
    }

    @SubscribeMapping("/webchat/user/logout")
    public void userLogout(Principal principal) {
        String login = principal.getName();
        users.remove(login);
        usersCount();
        messagingTemplate.convertAndSend("/webchat/user", users);
    }

    @SubscribeMapping("/webchat/user/app/on")
    @SendTo("/webchat/count")
    public int applicationOn() {
        return users.size();
    }

    public void usersCount() {
        messagingTemplate.convertAndSend("/webchat/count", users.size());
    }

    @SubscribeMapping("/websocket/connection/open")
    @SendTo("/websocket/status")
    public boolean socketConnectionOpen() {
        return true;
    }

    static class Message {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
