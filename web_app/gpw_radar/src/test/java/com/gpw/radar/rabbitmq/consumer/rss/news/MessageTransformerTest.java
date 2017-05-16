package com.gpw.radar.rabbitmq.consumer.rss.news;

import com.gpw.radar.domain.chat.ChatMessage;
import com.gpw.radar.elasticsearch.newsmessage.NewsMessage;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.service.builders.StockBuilder;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.amqp.core.Message;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.gpw.radar.rabbitmq.consumer.rss.news.MessageFactory.createRabbitMessage;
import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyObject;

public class MessageTransformerTest {

    private StockRepository mockedStockRepository = Mockito.mock(StockRepository.class);
    private final String headerName = "testHeader";
    private MessageTransformer objectUnderTest = new MessageTransformer(mockedStockRepository);

    @Test
    public void shouldCorrectlyTransformToChatMessage() throws IOException {
        //given
        Message msg = createRabbitMessage();

        //when
        ChatMessage chatMessages = objectUnderTest.transformMessage(msg);

        //then
        assertThat(chatMessages.getMessage()).isEqualTo("[2016-08-04T20:14]<a href=\"http://www.twiter.com/\" target=\"_blank\">test message</a>");
        assertThat(chatMessages.getLink()).isEqualTo("http://www.twiter.com/");
        assertThat(chatMessages.getUser().getId()).isEqualTo("h6ehbr4khohjr116k23pon9vojv66c3eab45aui6pmau3acq1b");
        assertThat(chatMessages.getUser().getLogin()).isEqualTo("system");
    }

    @Test
    public void shouldCorrectlyTransformToNewsMessage() throws IOException, InterruptedException {
        //given
        given(mockedStockRepository.findByStockName(anyObject())).willReturn(Optional.of(StockBuilder.sampleStock().build()));
        Message msg = createRabbitMessage();

        //when
        NewsMessage newsMessages = objectUnderTest.transformMessage(msg, headerName);

        //then
        assertThat(newsMessages.getMessage()).isEqualTo("test message");
        assertThat(newsMessages.getLink()).isEqualTo("http://www.twiter.com/");
        assertThat(newsMessages.getType()).isEqualTo(RssType.EBI);
        assertThat(newsMessages.getNewsDateTime()).isEqualTo(LocalDateTime.of(2016, 8, 4, 20, 14, 0));
    }

}
