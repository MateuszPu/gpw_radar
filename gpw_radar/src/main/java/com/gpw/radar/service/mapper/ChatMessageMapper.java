package com.gpw.radar.service.mapper;

import com.gpw.radar.domain.chat.ChatMessage;
import com.gpw.radar.web.rest.dto.chat.ChatMessageDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatMessageMapper {

    public List<ChatMessageDTO> mapToDto(List<ChatMessage> messages) {
        return messages.stream().map(e -> mapToDto(e)).collect(Collectors.toList());
    }

    public ChatMessageDTO mapToDto(ChatMessage message) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(orderMap);
        ChatMessageDTO chatMessageDTO = modelMapper.map(message, ChatMessageDTO.class);
        return chatMessageDTO;
    }

    private PropertyMap<ChatMessage, ChatMessageDTO> orderMap = new PropertyMap<ChatMessage, ChatMessageDTO>() {
        protected void configure() {
            map().setChatMessage(source.getMessage());
        }
    };
}
