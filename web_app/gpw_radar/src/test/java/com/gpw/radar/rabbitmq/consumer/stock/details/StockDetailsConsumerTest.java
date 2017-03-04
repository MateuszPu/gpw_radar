package com.gpw.radar.rabbitmq.consumer.stock.details;

import com.gpw.radar.elasticsearch.stockdetails.StockDetails;
import com.gpw.radar.dao.stockdetails.StockDetailsDAO;
import com.gpw.radar.elasticsearch.stockdetails.service.StockDetailsElasticSearchDAO;
import com.gpw.radar.rabbitmq.consumer.stock.details.database.Consumer;
import com.gpw.radar.service.auto.update.stockDetails.indicators.StandardStockIndicatorsCalculator;
import com.gpw.radar.service.mapper.JsonTransformer;
import com.gpw.radar.service.stock.StockService;
import com.gpw.radar.utils.StockDetailsAssert;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class StockDetailsConsumerTest {

    private StockDetailsDAO stockDetailsDaoEsMock = Mockito.mock(StockDetailsElasticSearchDAO.class);
    private StandardStockIndicatorsCalculator standardStockIndicatorsCalculatorMock = Mockito.mock(StandardStockIndicatorsCalculator.class);
    private StockService stockServiceMock = Mockito.mock(StockService.class);
    private Consumer objectUnderTest;

    @Before
    public void init() throws IOException {
        objectUnderTest = new Consumer(stockDetailsDaoEsMock, stockServiceMock, standardStockIndicatorsCalculatorMock,
            new JsonTransformer<StockDetails>());
    }

    @Test
    public void shouldCorrectParseStockDetails() throws IOException {
        //given
        given(stockDetailsDaoEsMock.findTopDate()).willReturn(LocalDate.of(2016, 11, 16));
        Message rabbitMessage = prepareRabbitMessage("2016-11-17");

        //when
        List<StockDetails> savedStocksDetails = objectUnderTest.parseStocksDetails(rabbitMessage);

        //then
        StockDetails tpeStockDetails = savedStocksDetails.stream()
            .filter(e -> e.getStock().getTicker().equalsIgnoreCase("tpe"))
            .findAny()
            .get();
        assertThat(savedStocksDetails.size()).isEqualTo(3);

        StockDetailsAssert.create(tpeStockDetails)
            .hasCorrectStockTicker("tpe")
            .hasCorrectStockShortName("TAURON")
            .hasCorrectOpenPrice(new BigDecimal("2.51"))
            .hasCorrectClosePrice(new BigDecimal("3.37"))
            .hasCorrectMaxPrice(new BigDecimal("2.61"))
            .hasCorrectMinPrice(new BigDecimal("2.37"))
            .hasCorrectDate(LocalDate.of(2016, 11, 17))
            .hasCorrectVolume(121013L)
            .hasCorrectTransactionNumber(29L);
    }

    @Test
    public void shouldCallAddMissingDataThreeTimes() throws IOException {
        //given
        given(stockDetailsDaoEsMock.findTopDate()).willReturn(LocalDate.of(2016, 11, 16));
        Message rabbitMessage = prepareRabbitMessage("2016-11-17");

        //when
        objectUnderTest.parseStocksDetails(rabbitMessage);

        //then
        verify(stockServiceMock, times(3)).addMissingData(any());
    }

    @Test
    public void shouldCallCalculateStockIndicators() throws IOException {
        //given
        given(stockDetailsDaoEsMock.findTopDate()).willReturn(LocalDate.of(2016, 11, 16));
        Message rabbitMessage = prepareRabbitMessage("2016-11-17");

        //when
        objectUnderTest.parseStocksDetails(rabbitMessage);


        //then
        verify(standardStockIndicatorsCalculatorMock, times(1)).calculateCurrentStockIndicators();
    }

    @Test
    public void shouldParseStockDetailsWhenMessageHasLaterDate() throws IOException {
        //given
        given(stockDetailsDaoEsMock.findTopDate()).willReturn(LocalDate.of(2016, 11, 16));
        Message rabbitMessage = prepareRabbitMessage("2016-11-17");

        //when
        List<StockDetails> savedStocksDetails = objectUnderTest.parseStocksDetails(rabbitMessage);

        //then
        assertThat(savedStocksDetails.isEmpty()).isFalse();
    }

    @Test
    public void shouldNotParseStockDetailsWhenMessageHasTheSameDate() throws IOException {
        //given
        given(stockDetailsDaoEsMock.findTopDate()).willReturn(LocalDate.of(2016, 11, 16));
        Message rabbitMessage = prepareRabbitMessage("2016-11-16");

        //when
        List<StockDetails> savedStocksDetails = objectUnderTest.parseStocksDetails(rabbitMessage);

        //then
        assertThat(savedStocksDetails.isEmpty()).isTrue();
    }

    @Test
    public void shouldNotParseStockDetailsWhenMessageHasTheEarlierDate() throws IOException {
        //given
        given(stockDetailsDaoEsMock.findTopDate()).willReturn(LocalDate.of(2016, 11, 16));
        Message rabbitMessage = prepareRabbitMessage("2016-11-15");

        //when
        List<StockDetails> savedStocksDetails = objectUnderTest.parseStocksDetails(rabbitMessage);

        //then
        assertThat(savedStocksDetails.isEmpty()).isTrue();
    }

    private Message prepareRabbitMessage(String date) {
        MessageProperties messageProperties = new MessageProperties();
        return new Message(prepareJsonMessageWithDate(date), messageProperties);
    }

    private byte[] prepareJsonMessageWithDate(String date) {
        String jsonStockDetailsOne = "{\"stock\":{\"ticker\":\"pzu\",\"shortName\":\"PZUSA\"},\"date\":\"" + date + "\",\"openPrice\":1.41,\"maxPrice\":1.31,\"minPrice\":1.37,\"closePrice\":1.37,\"volume\":1013,\"transactionsNumber\":9}";
        String jsonStockDetailsTwo = "{\"stock\":{\"ticker\":\"tpe\",\"shortName\":\"TAURON\"},\"date\":\"" + date + "\",\"openPrice\":2.51,\"maxPrice\":2.61,\"minPrice\":2.37,\"closePrice\":3.37,\"volume\":121013,\"transactionsNumber\":29}";
        String jsonStockDetailsThree = "{\"stock\":{\"ticker\":\"kgh\",\"shortName\":\"KGHM\"},\"date\":\"" + date + "\",\"openPrice\":22.51,\"maxPrice\":22.61,\"minPrice\":22.37,\"closePrice\":23.37,\"volume\":621013,\"transactionsNumber\":229}";
        String message = "[" + jsonStockDetailsOne + "," + jsonStockDetailsTwo + "," + jsonStockDetailsThree + "]";
        return message.getBytes();
    }
}
