package com.gpw.radar.rabbitmq.consumer.stock.details;

import com.gpw.radar.elasticsearch.domain.stockdetails.StockDetails;
import com.gpw.radar.elasticsearch.service.stockdetails.StockDetailsDaoEs;
import com.gpw.radar.rabbitmq.Mapper;
import com.gpw.radar.rabbitmq.consumer.stock.details.database.Consumer;
import com.gpw.radar.service.auto.update.stockDetails.indicators.StandardStockIndicatorsCalculator;
import com.gpw.radar.service.stock.StockService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StockDetailsConsumerTest {

    private StockDetailsDaoEs stockDetailsDaoEsMock;
    private StandardStockIndicatorsCalculator standardStockIndicatorsCalculatorMock;
    private StockService stockServiceMock;
    private Consumer objectUnderTest;

    @Before
    public void init() throws IOException {
        mockStockDetailsRepository();
        mockStandardStockIndicatorsCalculator();
        mockStockService();
        objectUnderTest = new Consumer(stockDetailsDaoEsMock, stockServiceMock, standardStockIndicatorsCalculatorMock,
            new Mapper<StockDetails, StockDetails>());
    }

    private void mockStockDetailsRepository() {
        stockDetailsDaoEsMock = Mockito.mock(StockDetailsDaoEs.class);
        when(stockDetailsDaoEsMock.findTopDate()).thenReturn(LocalDate.of(2016, 11, 16));
    }

    private void mockStockService() {
        stockServiceMock = Mockito.mock(StockService.class);

    }

    private void mockStandardStockIndicatorsCalculator() {
        standardStockIndicatorsCalculatorMock = Mockito.mock(StandardStockIndicatorsCalculator.class);
    }

    @Test
    public void shouldCorrectParseStockDetails() throws IOException {
        MessageProperties messageProperties = new MessageProperties();
        Message rabbitMessage = new Message(prepareJsonMessageWithDate("2016-11-17"), messageProperties);
        List<StockDetails> savedStocksDetails = objectUnderTest.parseStocksDetails(rabbitMessage);

        StockDetails tpeStockDetails = getStockByTicker("tpe", savedStocksDetails);
        assertThat(tpeStockDetails.getStock().getTicker()).isEqualTo("tpe");
        assertThat(tpeStockDetails.getStock().getShortName()).isEqualTo("TAURON");
        assertThat(tpeStockDetails.getVolume()).isEqualTo(121013L);
        assertThat(tpeStockDetails.getTransactionsNumber()).isEqualTo(29);
        assertThat(tpeStockDetails.getDate()).isEqualTo(LocalDate.of(2016, 11, 17));
        assertThat(tpeStockDetails.getClosePrice()).isEqualTo(new BigDecimal("3.37"));
        assertThat(tpeStockDetails.getMinPrice()).isEqualTo(new BigDecimal("2.37"));
        assertThat(tpeStockDetails.getMaxPrice()).isEqualTo(new BigDecimal("2.61"));
        assertThat(tpeStockDetails.getOpenPrice()).isEqualTo(new BigDecimal("2.51"));
        assertThat(savedStocksDetails.size()).isEqualTo(3);
    }

    @Test
    public void shouldCallAddMissingDataThreeTimes() throws IOException {
        MessageProperties messageProperties = new MessageProperties();
        Message rabbitMessage = new Message(prepareJsonMessageWithDate("2016-11-17"), messageProperties);
        objectUnderTest.parseStocksDetails(rabbitMessage);

        verify(stockServiceMock, times(3)).addMissingData(any());
    }

    @Test
    public void shouldCallCalculateStockIndicators() throws IOException {
        MessageProperties messageProperties = new MessageProperties();
        Message rabbitMessage = new Message(prepareJsonMessageWithDate("2016-11-17"), messageProperties);
        objectUnderTest.parseStocksDetails(rabbitMessage);

        verify(standardStockIndicatorsCalculatorMock, times(1)).calculateCurrentStockIndicators();
    }

    @Test
    public void shouldReturnNotEmptyList() throws IOException {
        MessageProperties messageProperties = new MessageProperties();
        Message rabbitMessage = new Message(prepareJsonMessageWithDate("2016-11-17"), messageProperties);
        List<StockDetails> savedStocksDetails = objectUnderTest.parseStocksDetails(rabbitMessage);

        assertThat(savedStocksDetails.isEmpty()).isFalse();
    }

    @Test
    public void shouldReturnEmptyList() throws IOException {
        MessageProperties messageProperties = new MessageProperties();
        Message rabbitMessage = new Message(prepareJsonMessageWithDate("2016-11-16"), messageProperties);
        List<StockDetails> savedStocksDetails = objectUnderTest.parseStocksDetails(rabbitMessage);

        assertThat(savedStocksDetails.isEmpty()).isTrue();
    }

    private StockDetails getStockByTicker(String ticker, List<StockDetails> savedStocksDetails) {
        return savedStocksDetails.stream()
            .filter(e -> e.getStock().getTicker().equalsIgnoreCase(ticker))
            .findAny()
            .get();
    }

    private byte[] prepareJsonMessageWithDate(String date) {
        String jsonStockDetailsOne = "{\"stock\":{\"ticker\":\"pzu\",\"shortName\":\"PZUSA\"},\"date\":\"" + date + "\",\"openPrice\":1.41,\"maxPrice\":1.31,\"minPrice\":1.37,\"closePrice\":1.37,\"volume\":1013,\"transactionsNumber\":9}";
        String jsonStockDetailsTwo = "{\"stock\":{\"ticker\":\"tpe\",\"shortName\":\"TAURON\"},\"date\":\"" + date + "\",\"openPrice\":2.51,\"maxPrice\":2.61,\"minPrice\":2.37,\"closePrice\":3.37,\"volume\":121013,\"transactionsNumber\":29}";
        String jsonStockDetailsThree = "{\"stock\":{\"ticker\":\"kgh\",\"shortName\":\"KGHM\"},\"date\":\"" + date + "\",\"openPrice\":22.51,\"maxPrice\":22.61,\"minPrice\":22.37,\"closePrice\":23.37,\"volume\":621013,\"transactionsNumber\":229}";
        String message = "[" + jsonStockDetailsOne + "," + jsonStockDetailsTwo + "," + jsonStockDetailsThree + "]";
        return message.getBytes();
    }
}
