package com.gpw.radar.web.sockets;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.gpw.radar.domain.User;
import com.gpw.radar.domain.chat.ChatMessage;
import com.gpw.radar.domain.chat.UserMessage;
import com.gpw.radar.repository.UserRepository;
import com.gpw.radar.repository.chat.MessageRepository;

@Controller
public class ChatController {

	@Inject
	private SimpMessagingTemplate template;

	@Inject
	private MessageRepository messageRepository;

	@Inject
	private UserRepository userRepository;

	private Set<String> users = new HashSet<String>();

	@MessageMapping("/webchat/send/message")
	@SendTo("/webchat/recive")
	public ChatMessage sendChatMessage(String message, Principal principal) {
		String userLogin = principal.getName();
		User currentUser = userRepository.findOneByLogin(userLogin).get();
		UserMessage msg = new UserMessage();
		msg.setMessage(convertMessage(message));
		msg.setUser(currentUser);
		msg.setUserLogin(userLogin);
		messageRepository.save(msg);
		return msg;
	}

	@MessageMapping("/webchat/user/login")
	@SendTo("/webchat/user")
	public void userLogin(Principal principal) throws InterruptedException {
		Thread.sleep(200);
		String login = principal.getName();
		users.add(login);
		usersCount();
		template.convertAndSend("/webchat/user", users);
	}

	@MessageMapping("/webchat/user/logout")
	public void userLogout(Principal principal) {
		String login = principal.getName();
		users.remove(login);
		usersCount();
		template.convertAndSend("/webchat/user", users);
	}

	@MessageMapping("/webchat/user/app/on")
	public void applicationOn() {
		usersCount();
	}

	public void sendSystemMessage(ChatMessage message) {
		UserMessage msg = new UserMessage();
		msg.setCreatedDate(message.getCreatedDate());
		msg.setMessage(message.getChatMessage());
		msg.setUser(userRepository.findOneByLogin("system").get());
		msg.setUserLogin("system");
		messageRepository.save(msg);
		template.convertAndSend("/webchat/recive", message);
	}

	public void usersCount() {
		template.convertAndSend("/webchat/count", users.size());
	}

	private String convertMessage(String message) {
		String firstConvert = message.substring(1, message.length() - 1);
		String secondConver = firstConvert.replace("\\\"", "\"");
		return secondConver;
	}

	// @Scheduled(cron="*/5 * * * * ?")
	// public void test() throws Exception {
	// Date ad = new Date();
	// template.convertAndSend("/webchat/user", "Data: " + ad.toString());
	// }
}