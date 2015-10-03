package com.gpw.radar.web.sockets;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class GreetingController {

	@Inject
	private SimpMessagingTemplate template;
	
	private Set<String> users = new HashSet<String>();

	@MessageMapping("/webchat/send/message")
	@SendTo("/webchat/recive")
	public String sendChatMessage(String message) {
		return message;
	}

	@MessageMapping("/webchat/user/login")
	@SendTo("/webchat/user")
	public void userLogin(Principal principal) throws InterruptedException {
		Thread.sleep(200);
		String login = principal.getName();
		users.add(login);
		template.convertAndSend("/webchat/user", users);
	}
	
	@MessageMapping("/webchat/user/logout")
	public void userLogout(Principal principal) {
		String login = principal.getName();
		users.remove(login);
		template.convertAndSend("/webchat/user", users);
	}

	// @Scheduled(cron="*/5 * * * * ?")
	// public void test() throws Exception {
	// Date ad = new Date();
	// template.convertAndSend("/webchat/user", "Data: " + ad.toString());
	// }

}