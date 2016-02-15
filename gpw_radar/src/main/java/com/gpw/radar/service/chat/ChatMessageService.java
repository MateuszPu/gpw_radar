package com.gpw.radar.service.chat;

import com.gpw.radar.domain.chat.ChatMessage;
import com.gpw.radar.domain.rss.NewsMessage;
import com.gpw.radar.repository.chat.ChatMessageRepository;
import com.gpw.radar.service.rss.RssObservable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ChatMessageService implements RssObserver, ChatMessageLoader {

    @Inject
    private ChatMessageRepository chatMessageRepository;

    @Inject
    private RssObservable rssParserService;

    @PostConstruct
    private void init() {
        rssParserService.addRssObserver(this);
    }

    public ResponseEntity<List<ChatMessage>> getLastMessages(int page) {
        List<ChatMessage> reverse = getMessages(page);
        Collections.reverse(reverse);
        return new ResponseEntity<List<ChatMessage>>(reverse, HttpStatus.OK);
    }

    public ResponseEntity<List<ChatMessage>> getOlderMessages(int page) {
        List<ChatMessage> messages = getMessages(page);
        return new ResponseEntity<List<ChatMessage>>(messages, HttpStatus.OK);
    }

    private List<ChatMessage> getMessages(int page) {
        Pageable pageRequest = new PageRequest(page, 10, Sort.Direction.DESC, "createdDate");
        Page<ChatMessage> messages = chatMessageRepository.findAll(pageRequest);
        List<ChatMessage> reverse = new ArrayList<ChatMessage>(messages.getContent());
        return reverse;
    }

    @Override
    public void updateRssNewsMessage(List<NewsMessage> parsedRssNewsMessage) {
        chatMessageRepository.save(parsedRssNewsMessage);
    }
}
