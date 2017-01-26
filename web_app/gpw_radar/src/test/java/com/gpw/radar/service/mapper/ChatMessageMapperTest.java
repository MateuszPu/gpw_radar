package com.gpw.radar.service.mapper;

import com.gpw.radar.domain.User;
import com.gpw.radar.domain.chat.ChatMessage;
import com.gpw.radar.service.mapper.custom.ChatMessageMapper;
import com.gpw.radar.web.rest.dto.chat.ChatMessageDTO;
import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class ChatMessageMapperTest {

    private ChatMessageMapper objectUnderTest = new ChatMessageMapper();

    @Test
    public void shouldCorrectlyMapToChatMessageDtoObject() {
        //given
        ChatMessage domainObject = new ChatMessage();
        domainObject.setCreatedDate(ZonedDateTime.of(2015, 5, 1, 15, 17, 54, 542, ZoneId.of("UTC")));
        domainObject.setMessage("message");
        User user = new User();
        user.setLogin("login");
        domainObject.setUser(user);

        //when
        ChatMessageDTO chatMessageDTO = objectUnderTest.mapToDto(domainObject);

        //then
        assertThat(chatMessageDTO.getCreatedDate()).isEqualTo(ZonedDateTime.of(2015, 5, 1, 15, 17, 54, 542, ZoneId.of("UTC")));
        assertThat(chatMessageDTO.getChatMessage()).isEqualTo("message");
        assertThat(chatMessageDTO.getUserLogin()).isEqualTo("login");
    }
}
