package com.gpw.radar.web.sockets;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import com.gpw.radar.domain.chat.ChatMessage;
import com.gpw.radar.service.chat.MessageService;

@Controller
public class ChatController {

	@Inject
	private SimpMessageSendingOperations messagingTemplate;

	@Inject
	private MessageService messageService;

	private Set<String> users = new HashSet<String>();

	@MessageMapping("/webchat/send/message")
	@SendTo("/webchat/recive")
	public ChatMessage sendChatMessage(String message, Principal principal) {
		ChatMessage msg = messageService.createUserMessage(message, principal);
		return msg;
	}

	@MessageMapping("/webchat/user/login")
	public void userLogin(Principal principal) throws InterruptedException {
		users.add(principal.getName());
		usersCount();
		messagingTemplate.convertAndSend("/webchat/user", users);
	}

	@MessageMapping("/webchat/user/logout")
	public void userLogout(Principal principal) {
		String login = principal.getName();
		users.remove(login);
		usersCount();
		messagingTemplate.convertAndSend("/webchat/user", users);
	}

	@MessageMapping("/webchat/user/app/on")
	public void applicationOn() {
		usersCount();
	}

	public void usersCount() {
		messagingTemplate.convertAndSend("/webchat/count", users.size());
	}
}