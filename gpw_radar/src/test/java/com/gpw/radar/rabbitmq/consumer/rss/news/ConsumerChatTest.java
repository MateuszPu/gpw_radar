package com.gpw.radar.rabbitmq.consumer.rss.news;

import com.gpw.radar.domain.chat.ChatMessage;
import com.gpw.radar.rabbitmq.consumer.rss.news.chat.Consumer;
import com.gpw.radar.repository.chat.ChatMessageRepository;
import com.gpw.radar.service.sockets.SocketMessageService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class ConsumerChatTest {

    private ChatMessageRepository mockedChatMessageRepository;
    private SocketMessageService mockedSocketMessageService;
    private Consumer objectUnderTest;

    @Before
    public void init() {
        mockChatMsgRepo();
        mockSocketRepo();
        Mapper<GpwNewsModel, ChatMessage> mapper = new Mapper();
        objectUnderTest = new Consumer(mockedChatMessageRepository, mockedSocketMessageService, mapper);
    }

    private void mockChatMsgRepo() {
        mockedChatMessageRepository = Mockito.mock(ChatMessageRepository.class);
    }

    private void mockSocketRepo() {
        mockedSocketMessageService = Mockito.mock(SocketMessageService.class);
    }

    @Test
    public void test() throws IOException, InterruptedException {
        //given
        String jsonMessageFirst = "{\"newsDateTime\":\"2016-08-04T20:14:00\",\"message\":\"test message\",\"link\":\"http://www.twiter.com/\"}";
        String message = "[" + jsonMessageFirst + "]";
        MessageProperties messageProperties = new MessageProperties();
        Message msg = new Message(message.getBytes(), messageProperties);

        //when
        List<ChatMessage> newsMessages = objectUnderTest.getUserMessages(msg);

        //then
        ChatMessage firstNewsMessages = newsMessages.get(0);
        assertThat(firstNewsMessages.getMessage()).isEqualTo("<a href=\"http://www.twiter.com/\" target=\"_blank\">test message</a>");
        assertThat(firstNewsMessages.getLink()).isEqualTo("http://www.twiter.com/");
        assertThat(firstNewsMessages.getUser().getLogin()).isEqualTo("system");
    }
}
