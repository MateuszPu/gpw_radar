package com.gpw.radar.service.chat;

import com.gpw.radar.domain.User;
import com.gpw.radar.domain.chat.ChatMessage;
import com.gpw.radar.repository.UserRepository;
import com.gpw.radar.repository.chat.ChatMessageRepository;
import com.gpw.radar.service.mapper.custom.ChatMessageMapper;
import com.gpw.radar.web.rest.dto.chat.ChatMessageDTO;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class ChatMessageServiceTest {

    private ChatMessageRepository chatMessageRepositoryMock = Mockito.mock(ChatMessageRepository.class);
    private ChatMessageMapper chatMessageMapper = new ChatMessageMapper();
    private UserRepository userRepositoryMock = Mockito.mock(UserRepository.class);
    private Principal principal = Mockito.mock(Principal.class);

    private final ChatMessageService objectUnderTest = new ChatMessageService(chatMessageRepositoryMock,
        chatMessageMapper, userRepositoryMock);

    @Test
    public void shouldThrowExceptionWhenMessageIsToBig() {
        //given
        String toBigMessage = "asdas saffsdafsdfsdafds sdafasdf sdfddffdfsdfsdfsdf sdfsdfsdfasdas saffsdafsdfsdafds sdafasdf sdfddffdfsdfsdfsdf sdfsdfsdf3333465";
        Exception catched = null;

        //when
        try {
            objectUnderTest.createUserMessage(toBigMessage, principal);
        } catch (Exception ex) {
            catched = ex;
        }

        //then
        assertThat(catched).isInstanceOf(IllegalArgumentException.class);
        verify(chatMessageRepositoryMock, times(0)).save(any(ChatMessage.class));
    }

    @Test
    public void shouldThrowExceptionWhenMessageIsToSmal() {
        //given
        String toBigMessage = "";
        Exception catched = null;

        //when
        try {
            objectUnderTest.createUserMessage(toBigMessage, principal);
        } catch (Exception ex) {
            catched = ex;
        }

        //then
        assertThat(catched).isInstanceOf(IllegalArgumentException.class);
        verify(chatMessageRepositoryMock, times(0)).save(any(ChatMessage.class));
    }

    @Test
    public void shouldCorrectlyCreateChatMessage() {
        //given
        String testMessage = "message";
        String principal_name = "principal_name";
        given(principal.getName()).willReturn(principal_name);
        Optional<User> user = Optional.of(createUser(principal_name));
        given(userRepositoryMock.findOneByLogin(principal_name)).willReturn(user);

        //when
        ChatMessage createdMessage = objectUnderTest.createUserMessage(testMessage, principal);

        //then
        verify(chatMessageRepositoryMock, times(1)).save(any(ChatMessage.class));
        assertThat(createdMessage.getMessage()).isEqualTo(testMessage);
        assertThat(createdMessage.getUser().getLogin()).isEqualTo(principal_name);
        assertThat(createdMessage.getUser().getEmail()).isEqualTo("email");
        assertThat(createdMessage.getUser().getId()).isEqualTo("id");
    }

    @Test
    public void shouldCorrectlyReturnChatMessageDto() {
        //given
        Pageable pageRequest = new PageRequest(0, 10, Sort.Direction.DESC, "createdDate");
        given(chatMessageRepositoryMock.findAll(pageRequest)).willReturn(chatMessagePage());
        ZonedDateTime firstDate = ZonedDateTime.of(2016, 10, 1, 12, 14, 32, 0, ZoneId.systemDefault());
        ZonedDateTime lastDate = ZonedDateTime.of(2016, 10, 10, 12, 14, 32, 0, ZoneId.systemDefault());

        //when
        ResponseEntity<List<ChatMessageDTO>> lastMessages = objectUnderTest.getLastMessages(0);

        //then
        assertThat(lastMessages.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(lastMessages.getBody().isEmpty()).isFalse();
    }

    private Page<ChatMessage> chatMessagePage() {
        List<ChatMessage> chatMessages = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setId("Id" + i);
            chatMessage.setMessage("Message" + i);
            chatMessage.setUser(createUser("login"));
            chatMessage.setCreatedDate(ZonedDateTime.of(2016, 10, i + 1, 12, 14, 32, 0, ZoneId.systemDefault()));
            chatMessages.add(chatMessage);
        }
        return new PageImpl<>(chatMessages);
    }

    private User createUser(String login) {
        User user = new User();
        user.setLogin(login);
        user.setEmail("email");
        user.setId("id");
        return user;
    }

}
