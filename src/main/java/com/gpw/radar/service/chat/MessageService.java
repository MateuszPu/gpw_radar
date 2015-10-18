package com.gpw.radar.service.chat;

import java.security.Principal;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.gpw.radar.domain.User;
import com.gpw.radar.domain.chat.ChatMessage;
import com.gpw.radar.domain.chat.UserMessage;
import com.gpw.radar.repository.UserRepository;
import com.gpw.radar.repository.chat.MessageRepository;

@Service
public class MessageService {

	@Inject
	private MessageRepository messageRepository;

	@Inject
	private UserRepository userRepository;

	public ChatMessage createUserMessage(String message, Principal principal) {
		String userLogin = principal.getName();
		User currentUser = userRepository.findOneByLogin(userLogin).get();
		UserMessage msg = new UserMessage();
		msg.setMessage(convertMessage(message));
		msg.setUser(currentUser);
		msg.setUserLogin(userLogin);
		messageRepository.save(msg);
		return msg;
	}

	private String convertMessage(String message) {
		String firstConvert = message.substring(1, message.length() - 1);
		String secondConver = firstConvert.replace("\\\"", "\"");
		return secondConver;
	}

}