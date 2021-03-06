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
        List<ChatMessage> chatMessages = objectUnderTest.transformMessage(msg);

        //then
        ChatMessage firstMessage = chatMessages.get(0);
        assertThat(firstMessage.getMessage()).isEqualTo("[2016-08-04T20:14]<a href=\"http://www.twiter.com/\" target=\"_blank\">test message</a>");
        assertThat(firstMessage.getLink()).isEqualTo("http://www.twiter.com/");
        assertThat(firstMessage.getUser().getId()).isEqualTo("h6ehbr4khohjr116k23pon9vojv66c3eab45aui6pmau3acq1b");
        assertThat(firstMessage.getUser().getLogin()).isEqualTo("system");
    }

    @Test
    public void shouldCorrectlyTransformToNewsMessage() throws IOException, InterruptedException {
        //given
        given(mockedStockRepository.findByStockName(anyObject())).willReturn(Optional.of(StockBuilder.sampleStock().build()));
        Message msg = createRabbitMessage();

        //when
        List<NewsMessage> newsMessages = objectUnderTest.transformMessage(msg, headerName);

        //then
        NewsMessage newsMessageWithOutStock = newsMessages.stream().filter(e -> e.getStock() == null).findAny().get();
        assertThat(newsMessageWithOutStock.getMessage()).isEqualTo("test message");
        assertThat(newsMessageWithOutStock.getLink()).isEqualTo("http://www.twiter.com/");
        assertThat(newsMessageWithOutStock.getType()).isEqualTo(RssType.EBI);
        assertThat(newsMessageWithOutStock.getNewsDateTime()).isEqualTo(LocalDateTime.of(2016, 8, 4, 20, 14, 0));

        NewsMessage newsMessageWithStock = newsMessages.stream().filter(e -> e.getStock() != null).findAny().get();
        assertThat(newsMessageWithStock.getMessage()).isEqualTo("RAWLPLUG SA test message two");
        assertThat(newsMessageWithStock.getLink()).isEqualTo("http://www.google.pl/");
        assertThat(newsMessageWithStock.getType()).isEqualTo(RssType.EBI);
        assertThat(newsMessageWithStock.getNewsDateTime()).isEqualTo(LocalDateTime.of(2016, 8, 1, 20, 0, 0));
        assertThat(newsMessageWithStock.getStock().getTicker()).isEqualTo("kgh");
        assertThat(newsMessageWithStock.getStock().getName()).isEqualTo("KGH name");
        assertThat(newsMessageWithStock.getStock().getShortName()).isEqualTo("KGH short name");
    }

}
