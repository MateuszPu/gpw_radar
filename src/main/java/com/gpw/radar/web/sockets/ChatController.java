package com.gpw.radar.web.sockets;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.gpw.radar.domain.chat.ChatMessage;
import com.gpw.radar.repository.UserRepository;
import com.gpw.radar.service.chat.MessageService;

@Controller
public class ChatController {

	@Inject
	private SimpMessagingTemplate template;

	@Inject
	private MessageService messageService;
	
	@Inject
	private UserRepository userRepository;

	private Set<String> users = new HashSet<String>();

	@MessageMapping("/webchat/send/message")
	@SendTo("/webchat/recive")
	public ChatMessage sendChatMessage(String message, Principal principal) {
		ChatMessage msg = messageService.createUserMessage(message, principal);
		return msg;
	}

	@MessageMapping("/webchat/user/login")
	public void userLogin(String login) throws InterruptedException {
		users.add(login);
		if(userRepository.findOneByLogin(login) == null){
			throw new IllegalAccessError();
		}
		usersCount();
		template.convertAndSend("/webchat/user", users);
	}

	@MessageMapping("/webchat/user/logout")
	public void userLogout(String login) {
		users.remove(login);
		usersCount();
		template.convertAndSend("/webchat/user", users);
	}

	@MessageMapping("/webchat/user/app/on")
	public void applicationOn() {
		usersCount();
	}

	public void usersCount() {
		template.convertAndSend("/webchat/count", users.size());
	}
}