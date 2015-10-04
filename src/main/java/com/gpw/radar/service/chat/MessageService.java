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

import com.gpw.radar.domain.chat.Message;
import com.gpw.radar.repository.chat.MessageRepository;

@Service
public class MessageService {

	@Inject
	private MessageRepository messageRepository;
	
	public ResponseEntity<List<Message>> getLastMessages(int page) {
		Pageable pageRequest = new PageRequest(page, 10, Sort.Direction.DESC, "createdDate");
		Page<Message> messages = messageRepository.findAll(pageRequest);
		List<Message> reverse = new ArrayList<Message>(messages.getContent());
		Collections.reverse(reverse);
		return new ResponseEntity<List<Message>>(reverse, HttpStatus.OK);
	}

}
