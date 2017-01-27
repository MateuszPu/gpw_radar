package com.gpw.radar.rabbitmq.consumer.rss.news;

import com.gpw.radar.rabbitmq.consumer.rss.news.database.Consumer;
import com.gpw.radar.repository.rss.NewsMessageRepository;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static com.gpw.radar.rabbitmq.consumer.rss.news.MessageFactory.createRabbitMessage;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DatabaseConsumerTest {

    private NewsMessageRepository newsMessageRepositoryMock = Mockito.mock(NewsMessageRepository.class);
    private MessageTransformer messageTransformerMock = Mockito.mock(MessageTransformer.class);
    private Consumer objectUnderTest = new Consumer(newsMessageRepositoryMock, "not_important", messageTransformerMock);

    @Test
    public void shouldSaveNewsMessage() throws IOException, InterruptedException {
        objectUnderTest.consumeMessage(createRabbitMessage());
        verify(newsMessageRepositoryMock, times(1)).save(anyCollection());
    }

}

