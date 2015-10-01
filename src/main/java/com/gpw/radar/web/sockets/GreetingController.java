package com.gpw.radar.web.sockets;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
public class GreetingController {

	@Autowired 
	private SimpMessagingTemplate template; 

    @MessageMapping("/webchat")
    @SendTo("/chat/send/message")
    public String greeting(String message) throws Exception {
        Thread.sleep(3000); // simulated delay
        String a = message;
        System.out.println(a);
        return "Hello, " + message + "!";
    }
    
//    @Scheduled(cron="*/5 * * * * ?")
//    public void test() throws Exception {
//    	Date ad = new Date();
//    	template.convertAndSend("/topic/dupa", "Data: " + ad.toString());
//    }

}