package com.gpw.radar.rabbitmq.consumer.rss.news;

import com.gpw.radar.elasticsearch.newsmessage.NewsMessage;
import com.gpw.radar.elasticsearch.stockdetails.Stock;
import com.gpw.radar.rabbitmq.consumer.rss.news.mail.Consumer;
import com.gpw.radar.service.mail.MailService;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.gpw.radar.rabbitmq.consumer.rss.news.MessageFactory.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MailConsumerTest {

    private MailService mailServiceMock = Mockito.mock(MailService.class);
    private MessageTransformer messageTransformerMock = Mockito.mock(MessageTransformer.class);
    private Consumer objectUnderTest = new Consumer(mailServiceMock, "notImportant", messageTransformerMock);

    @Test
    public void shouldSendEmailToUser() throws IOException, InterruptedException {

        //given
        NewsMessage newsMessageMock = new NewsMessage();
        newsMessageMock.setStock(new Stock());
        List<NewsMessage> newsMessagesMock = Arrays.asList(new NewsMessage(), new NewsMessage(), newsMessageMock);
        given(messageTransformerMock.transformMessage(any(), anyString())).willReturn(newsMessagesMock);

        //when
        objectUnderTest.consumeMessage(createRabbitMessage());

        //then
        verify(messageTransformerMock, times(1)).transformMessage(any(), anyString());
        verify(messageTransformerMock, times(3)).transformToChatMessageContent(anyString(), anyString());
        verify(mailServiceMock, times(1)).informUserAboutStockNews(any());
    }
}
