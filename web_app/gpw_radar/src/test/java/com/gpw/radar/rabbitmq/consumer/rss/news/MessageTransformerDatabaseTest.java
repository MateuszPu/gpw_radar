package com.gpw.radar.rabbitmq.consumer.rss.news;

import com.gpw.radar.domain.rss.NewsMessage;
import com.gpw.radar.rabbitmq.MessageTransformer;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.service.builders.StockBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

public class MessageTransformerDatabaseTest {

    private StockRepository mockedStockRepository;
    private final String headerName = "testHeader";
    private MessageTransformer objectUnderTest;

    @Before
    public void init() {
        mockStockRepo();
        objectUnderTest = new MessageTransformer(mockedStockRepository);
    }

    private void mockStockRepo() {
        mockedStockRepository = Mockito.mock(StockRepository.class);
        when(mockedStockRepository.findByStockName(anyObject())).thenReturn(Optional.of(StockBuilder.sampleStock().build()));
    }

    @Test
    public void transformMessageTest() {
        String message = objectUnderTest.transformMessage("www.onet.pl", "message test");
        assertThat(message).isEqualTo("<a href=\"www.onet.pl\" target=\"_blank\">message test</a>");
    }

    @Test
    public void getNewsMessageTest() throws IOException, InterruptedException {
        //given
        String jsonMessageFirst = "{\"newsDateTime\":\"2016-08-04T20:14:00\",\"message\":\"test message\",\"link\":\"http://www.twiter.com/\"}";
        String jsonMessageSecond = "{\"newsDateTime\":\"2016-08-01T20:00:00\",\"message\":\"RAWLPLUG SA test message two\",\"link\":\"http://www.google.pl/\"}";
        String message = "[" + jsonMessageFirst + ", " + jsonMessageSecond + "]";
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.getHeaders().put("testHeader", RssType.EBI.name());
        Message msg = new Message(message.getBytes(), messageProperties);

        //when
        List<NewsMessage> newsMessages = objectUnderTest.getNewsMessages(msg, headerName);

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
        assertThat(newsMessageWithStock.getStock().getStockName()).isEqualTo("KGH name");
        assertThat(newsMessageWithStock.getStock().getStockShortName()).isEqualTo("KGH short name");
    }
}
