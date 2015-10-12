package com.gpw.radar.service.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.gpw.radar.domain.chat.UserMessage;
import com.gpw.radar.repository.chat.MessageRepository;

@Service
public class MessageService {

	@Inject
	private MessageRepository messageRepository;
	
	public ResponseEntity<List<UserMessage>> getLastMessages(int page) {
		List<UserMessage> reverse = getMessages(page);
		Collections.reverse(reverse);
		return new ResponseEntity<List<UserMessage>>(reverse, HttpStatus.OK);
	}

	public ResponseEntity<List<UserMessage>> getOlderMessages(int page) {
		List<UserMessage> messages = getMessages(page);
		return new ResponseEntity<List<UserMessage>>(messages, HttpStatus.OK);
	}
	
	private List<UserMessage> getMessages(int page) {
		Pageable pageRequest = new PageRequest(page, 10, Sort.Direction.DESC, "createdDate");
		Page<UserMessage> messages = messageRepository.findAll(pageRequest);
		List<UserMessage> reverse = new ArrayList<UserMessage>(messages.getContent());
		return reverse;
	}

}
