package com.gpw.radar.service.chat;

import com.gpw.radar.domain.User;
import com.gpw.radar.domain.chat.ChatMessage;
import com.gpw.radar.repository.UserRepository;
import com.gpw.radar.repository.chat.ChatMessageRepository;
import com.gpw.radar.service.mapper.custom.ChatMessageMapper;
import com.gpw.radar.web.rest.dto.chat.ChatMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageMapper chatMessageMapper;
    private final UserRepository userRepository;

    @Autowired
    public ChatMessageService(ChatMessageRepository chatMessageRepository, ChatMessageMapper chatMessageMapper,
                              UserRepository userRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatMessageMapper = chatMessageMapper;
        this.userRepository = userRepository;
    }

    public ResponseEntity<List<ChatMessageDTO>> getLastMessages(int page) {
        Pageable pageRequest = new PageRequest(page, 10, Sort.Direction.DESC, "createdDate");
        Page<ChatMessage> messages = chatMessageRepository.findAll(pageRequest);
        List<ChatMessage> reverse = new ArrayList<>(messages.getContent());
        List<ChatMessageDTO> chatMessagesDto = chatMessageMapper.mapToDto(reverse);
        return new ResponseEntity<>(chatMessagesDto, HttpStatus.OK);
    }

    public ChatMessage createUserMessage(String message, Principal principal) {
        if ( message.isEmpty() || message.length() > 128) {
            throw new IllegalArgumentException("Message should have length of 1 to 128");
        }
        String userLogin = principal.getName();
        User currentUser = userRepository.findOneByLogin(userLogin).get();
        ChatMessage msg = new ChatMessage();
        msg.setMessage(message);
        msg.setUser(currentUser);
        chatMessageRepository.save(msg);
        return msg;
    }
}
