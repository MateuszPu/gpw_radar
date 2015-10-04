package com.gpw.radar.web.rest.chat;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gpw.radar.domain.chat.Message;
import com.gpw.radar.security.AuthoritiesConstants;
import com.gpw.radar.service.chat.MessageService;

/**
 * REST controller for managing chat messages.
 */
@RestController
@RequestMapping("/api")
public class MessageResource {

	@Inject
	private MessageService messageService;
	
	@RequestMapping(value = "/chat/last/10/messages", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed(AuthoritiesConstants.USER)
	public ResponseEntity<List<Message>> getLastMessages(@RequestParam int page) {
		return messageService.getLastMessages(page);
	}
}
