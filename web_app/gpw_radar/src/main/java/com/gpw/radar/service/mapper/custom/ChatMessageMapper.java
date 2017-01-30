package com.gpw.radar.service.mapper.custom;

import com.gpw.radar.domain.chat.ChatMessage;
import com.gpw.radar.service.mapper.DtoMapper;
import com.gpw.radar.web.rest.dto.chat.ChatMessageDTO;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageMapper extends DtoMapper<ChatMessage, ChatMessageDTO> {

    public ChatMessageMapper() {
        super(ChatMessageDTO.class);
        super.modelMapper.addMappings(orderMap);
    }

    private PropertyMap<ChatMessage, ChatMessageDTO> orderMap = new PropertyMap<ChatMessage, ChatMessageDTO>() {
        protected void configure() {
            map().setChatMessage(source.getMessage());
            map().setUserLogin(source.getUser().getLogin());
        }
    };

    public List<ChatMessageDTO> mapToDto(List<ChatMessage> source) {
        return super.mapToDto(source);
    }

    public ChatMessageDTO mapToDto(ChatMessage source) {
        return super.mapToDto(source);
    }
}
