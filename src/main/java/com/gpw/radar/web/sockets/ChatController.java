package com.gpw.radar.web.sockets;

import com.gpw.radar.domain.chat.ChatMessage;
import com.gpw.radar.service.chat.MessageService;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

@Controller
public class ChatController {

	@Inject
	private SimpMessageSendingOperations messagingTemplate;

	@Inject
	private MessageService messageService;

	private Set<String> users = new HashSet<String>();

	@SubscribeMapping("/webchat/send/message")
	@SendTo("/webchat/recive")
	public ChatMessage sendChatMessage(Message message, Principal principal) {
		ChatMessage msg = messageService.createUserMessage(message.getMessage(), principal);
		return msg;
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
	public void applicationOn() {
		usersCount();
	}

	public void usersCount() {
		messagingTemplate.convertAndSend("/webchat/count", users.size());
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
