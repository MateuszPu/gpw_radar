package com.gpw.radar.rabbitmq.consumer.rss.news;

import com.gpw.radar.domain.chat.ChatMessage;
import com.gpw.radar.rabbitmq.consumer.rss.news.chat.Consumer;
import com.gpw.radar.repository.chat.ChatMessageRepository;
import com.gpw.radar.service.sockets.SocketMessageService;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;

import static com.gpw.radar.rabbitmq.consumer.rss.news.MessageFactory.createRabbitMessage;
import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ChatConsumerTest {

    private MessageTransformer messageTransformerMock = Mockito.mock(MessageTransformer.class);
    private ChatMessageRepository chatMessageRepositoryMock = Mockito.mock(ChatMessageRepository.class);
    private SocketMessageService socketMessageServiceMock = Mockito.mock(SocketMessageService.class);
    private Consumer objectUnderTest = new Consumer(chatMessageRepositoryMock, socketMessageServiceMock, messageTransformerMock);

    @Test
    public void shouldSaveAndSendChatMessage() throws IOException, InterruptedException {
        //given
        ArgumentCaptor<ChatMessage> argument = ArgumentCaptor.forClass(ChatMessage.class);
        ChatMessage chatMessagesMock = createMessage(ZonedDateTime.of(2016, 1, 5, 17, 6, 0, 0, ZoneId.systemDefault()), "id1");
        given(chatMessageRepositoryMock.save(any(ChatMessage.class))).willReturn(chatMessagesMock);

        //when
        objectUnderTest.consumeMessage(createRabbitMessage());

        //then
        verify(socketMessageServiceMock, times(1)).sendToChat(argument.capture());
        ZonedDateTime createdDateOfMassage = argument.getAllValues().get(0).getCreatedDate();
        assertThat(createdDateOfMassage.toLocalDate()).isEqualTo(LocalDate.of(2016, 1, 5));
    }

    private ChatMessage createMessage(ZonedDateTime dateTime, String id) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setCreatedDate(dateTime);
        chatMessage.setId(id);
        return chatMessage;
    }

}
