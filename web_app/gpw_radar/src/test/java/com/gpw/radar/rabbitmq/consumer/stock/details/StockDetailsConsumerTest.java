package com.gpw.radar.rabbitmq.consumer.stock.details;

import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.rabbitmq.Mapper;
import com.gpw.radar.rabbitmq.consumer.stock.details.database.Consumer;
import com.gpw.radar.repository.stock.StockDetailsRepository;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.service.builders.StockBuilder;
import com.gpw.radar.service.parser.web.UrlStreamsGetterService;
import com.gpw.radar.service.parser.web.stock.StockDataDetailsWebParser;
import com.gpw.radar.service.parser.web.stock.StooqDataParserServiceData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

public class StockDetailsConsumerTest {

    private StockDetailsRepository mockedStockDetailsRepository;
    private StockRepository mockedStockRepository;
    private StockDataDetailsWebParser detailsParser = new StooqDataParserServiceData();
    private UrlStreamsGetterService mockedUrlStreamsGetterService;
    private Mapper<StockDetailsModel, StockDetails> mapper = new Mapper<>();
    private Consumer objectUnderTest;

    @Before
    public void init() throws IOException {
        mockStockDetailsRepository();
        mockStockRepository();
        mockUrlStreamsGetterService();
        objectUnderTest = new Consumer(mockedStockDetailsRepository, mockedStockRepository,
            detailsParser, mockedUrlStreamsGetterService, mapper);
    }

    private void mockStockDetailsRepository() {
        mockedStockDetailsRepository = Mockito.mock(StockDetailsRepository.class);
        when(mockedStockDetailsRepository.findTopDate()).thenReturn(LocalDate.of(2016, 11, 16));
    }


    private void mockStockRepository() {
        mockedStockRepository = Mockito.mock(StockRepository.class);
        List<Stock> stocks = new ArrayList<>();
        Stock pzu = StockBuilder.sampleStock().id("1").stockName("PZU UBEZPIECZENIA").stockShortName("PZU SA").ticker("pzu").build();
        Stock tpe = StockBuilder.sampleStock().id("2").stockName("TAURON ENERGIA").stockShortName("TAURON").ticker("tpe").build();
        stocks.add(pzu);
        stocks.add(tpe);
        when(mockedStockRepository.findAll()).thenReturn(stocks);
    }

    private void mockUrlStreamsGetterService() throws IOException {
        String htmlStooqSite = "/stocks_data/stooqSite.html";
        try (InputStream in = getClass().getResourceAsStream(htmlStooqSite)) {
            Document stooqSite = Jsoup.parse(in, null, "uri cannot be null");
            mockedUrlStreamsGetterService = Mockito.mock(UrlStreamsGetterService.class);
            when(mockedUrlStreamsGetterService.getDocFromUrl(anyObject())).thenReturn(stooqSite);
        }
    }

    @Test
    public void assertThatConsumerGetMessageAndProcessIt() throws IOException, InterruptedException {
        MessageProperties messageProperties = new MessageProperties();
        Message rabbitMessage = new Message(prepareJsonMessage(), messageProperties);
        List<StockDetails> savedStocksDetails = objectUnderTest.processMessage(rabbitMessage);

        StockDetails tpeStockDetails = getStockByTicker("tpe", savedStocksDetails);
        assertThat(tpeStockDetails.getStock().getId()).isEqualTo("2");
        assertThat(tpeStockDetails.getStock().getTicker()).isEqualTo("tpe");
        assertThat(tpeStockDetails.getStock().getStockName()).isEqualTo("TAURON ENERGIA");
        assertThat(tpeStockDetails.getStock().getStockShortName()).isEqualTo("TAURON");
        assertThat(tpeStockDetails.getVolume()).isEqualTo(121013L);
        assertThat(tpeStockDetails.getTransactionsNumber()).isEqualTo(29);
        assertThat(tpeStockDetails.getDate()).isEqualTo(LocalDate.of(2016, 11, 17));
        assertThat(tpeStockDetails.getClosePrice()).isEqualTo(new BigDecimal("3.37"));
        assertThat(tpeStockDetails.getMinPrice()).isEqualTo(new BigDecimal("2.37"));
        assertThat(tpeStockDetails.getMaxPrice()).isEqualTo(new BigDecimal("2.61"));
        assertThat(tpeStockDetails.getOpenPrice()).isEqualTo(new BigDecimal("2.51"));

        StockDetails kghStockDetails = getStockByTicker("kgh", savedStocksDetails);
        assertThat(kghStockDetails.getStock().getId()).isEqualTo(null);
        assertThat(kghStockDetails.getStock().getTicker()).isEqualTo("kgh");
        assertThat(kghStockDetails.getStock().getStockName()).isEqualTo("KGHM POLSKA MIEDŹ SA");
        assertThat(kghStockDetails.getStock().getStockShortName()).isEqualTo("KGHM");
        assertThat(kghStockDetails.getVolume()).isEqualTo(621013L);
        assertThat(kghStockDetails.getTransactionsNumber()).isEqualTo(229);
        assertThat(kghStockDetails.getDate()).isEqualTo(LocalDate.of(2016, 11, 17));
        assertThat(kghStockDetails.getClosePrice()).isEqualTo(new BigDecimal("23.37"));
        assertThat(kghStockDetails.getMinPrice()).isEqualTo(new BigDecimal("22.37"));
        assertThat(kghStockDetails.getMaxPrice()).isEqualTo(new BigDecimal("22.61"));
        assertThat(kghStockDetails.getOpenPrice()).isEqualTo(new BigDecimal("22.51"));
    }

    private StockDetails getStockByTicker(String ticker, List<StockDetails> savedStocksDetails) {
        return savedStocksDetails.stream()
            .filter(e -> e.getStock().getTicker().equalsIgnoreCase(ticker))
            .findAny()
            .get();
    }

    private byte[] prepareJsonMessage() {
        String jsonStockDetailsOne = "{\"stockTicker\":\"PZU\",\"stockName\":\"PZUSA\",\"date\":\"2016-11-17\",\"openPrice\":1.41,\"maxPrice\":1.31,\"minPrice\":1.37,\"closePrice\":1.37,\"volume\":1013,\"transactionsNumber\":9}";
        String jsonStockDetailsTwo = "{\"stockTicker\":\"TPE\",\"stockName\":\"TAURON\",\"date\":\"2016-11-17\",\"openPrice\":2.51,\"maxPrice\":2.61,\"minPrice\":2.37,\"closePrice\":3.37,\"volume\":121013,\"transactionsNumber\":29}";
        String jsonStockDetailsThree = "{\"stockTicker\":\"KGH\",\"stockName\":\"KGHM\",\"date\":\"2016-11-17\",\"openPrice\":22.51,\"maxPrice\":22.61,\"minPrice\":22.37,\"closePrice\":23.37,\"volume\":621013,\"transactionsNumber\":229}";
        String message = "[" + jsonStockDetailsOne + "," + jsonStockDetailsTwo + "," + jsonStockDetailsThree + "]";
        return message.getBytes();
    }
}
