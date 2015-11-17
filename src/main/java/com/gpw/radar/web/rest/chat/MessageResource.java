package com.gpw.radar.web.rest.chat;

import com.gpw.radar.domain.chat.ChatMessage;
import com.gpw.radar.security.AuthoritiesConstants;
import com.gpw.radar.service.chat.ChatMessageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.util.List;

/**
 * REST controller for managing chat messages.
 */
@RestController
@RequestMapping("/api/chat")
@RolesAllowed(AuthoritiesConstants.USER)
public class MessageResource {

	@Inject
	private ChatMessageService chatMessageService;

	@RequestMapping(value = "/last/10/messages", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ChatMessage>> getLastMessages(@RequestParam int page) {
		return chatMessageService.getLastMessages(page);
	}

	@RequestMapping(value = "/older/messages", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ChatMessage>> getOlderMessages(@RequestParam int page) {
		return chatMessageService.getOlderMessages(page);
	}
}
