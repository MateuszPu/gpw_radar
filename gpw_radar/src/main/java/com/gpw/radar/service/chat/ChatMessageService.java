package com.gpw.radar.service.chat;

import com.gpw.radar.domain.chat.ChatMessage;
import com.gpw.radar.domain.rss.NewsMessage;
import com.gpw.radar.repository.chat.ChatMessageRepository;
import com.gpw.radar.service.rss.RssObservable;
import com.gpw.radar.web.rest.dto.chat.ChatMessageDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.lang.reflect.Type;
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

    public ResponseEntity<List<ChatMessageDTO>> getLastMessages(int page) {
        List<ChatMessageDTO> reverse = getMessages(page);
        Collections.reverse(reverse);

        return new ResponseEntity<List<ChatMessageDTO>>(reverse, HttpStatus.OK);
    }

    public ResponseEntity<List<ChatMessageDTO>> getOlderMessages(int page) {
        List<ChatMessageDTO> messages = getMessages(page);
        return new ResponseEntity<List<ChatMessageDTO>>(messages, HttpStatus.OK);
    }

    private List<ChatMessageDTO> getMessages(int page) {
        Pageable pageRequest = new PageRequest(page, 10, Sort.Direction.DESC, "createdDate");
        Page<ChatMessage> messages = chatMessageRepository.findAll(pageRequest);
        List<ChatMessage> reverse = new ArrayList<ChatMessage>(messages.getContent());

        ModelMapper modelMapper = new ModelMapper();
        Type dtoType = new TypeToken<List<ChatMessageDTO>>() {}.getType();
        return modelMapper.map(reverse, dtoType);
    }

    @Override
    public void updateRssNewsMessage(List<NewsMessage> parsedRssNewsMessage) {
        chatMessageRepository.save(parsedRssNewsMessage);
    }
}
