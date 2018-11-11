package com.gpw.radar.service.correlation;

import com.gpw.radar.elasticsearch.stockdetails.Stock;
import com.gpw.radar.elasticsearch.stockdetails.StockDetails;
import com.gpw.radar.dao.stockdetails.StockDetailsDAO;
import com.gpw.radar.elasticsearch.stockdetails.dao.StockDetailsEsDAO;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.service.builders.StockDetailsEsBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(value = Parameterized.class)
public class CorrelationServiceTest {

    private StockDetailsDAO stockDetailsDaoEsMock = Mockito.mock(StockDetailsEsDAO.class);
    private StockRepository stockRepositoryMock = Mockito.mock(StockRepository.class);
    private CorrelationService objectUnderTest = new CorrelationService(stockDetailsDaoEsMock, stockRepositoryMock);

    private CorrelationType correlationType;

    public CorrelationServiceTest(CorrelationType correlationType) {
        this.correlationType = correlationType;
    }

    @Parameterized.Parameters(name = "Test for correlation type: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {CorrelationType.KENDALLS},
            {CorrelationType.PEARSONS},
            {CorrelationType.SPEARMANS}
        });
    }

    @Test
    public void shouldReturnBadResponseWhenProvidedInvalidPeriod() {
        //given
        int invalidPeriod = 5;
        String ticker = "ticker";

        //when
        ResponseEntity<List<CorrelationResult>> response = objectUnderTest.computeCorrelation(ticker, invalidPeriod,
            correlationType);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void resultShouldBeSizeFourForDataSameLength() {
        //given
        int period = 10;
        Pageable pageable = new PageRequest(0, period);
        given(stockRepositoryMock.findAllTickerNotEquals("a")).willReturn(new HashSet<>(Arrays.asList("b", "c", "d", "e")));
        given(stockDetailsDaoEsMock.findByStockTickerOrderByDateDesc("a", pageable)).willReturn(createRandomData("a", period));
        given(stockDetailsDaoEsMock.findByStockTickerOrderByDateDesc("b", pageable)).willReturn(createRandomData("b", period));
        given(stockDetailsDaoEsMock.findByStockTickerOrderByDateDesc("c", pageable)).willReturn(createRandomData("c", period));

        //when
        List<CorrelationResult> correlationForA = new ArrayList<>(objectUnderTest.computeCorrelation("a", period,
            correlationType).getBody());

        //then
        assertThat(correlationForA.size()).isEqualTo(2);
    }

    @Test
    public void resultShouldBeSizeTwoForDataDifferentLength() {
        //given
        int period = 10;
        Pageable pageable = new PageRequest(0, period);
        given(stockRepositoryMock.findAllTickerNotEquals("a")).willReturn(new HashSet<>(Arrays.asList("b", "c", "d", "e")));
        given(stockDetailsDaoEsMock.findByStockTickerOrderByDateDesc("a", pageable)).willReturn(createRandomData("a", period));
        given(stockDetailsDaoEsMock.findByStockTickerOrderByDateDesc("b", pageable)).willReturn(createRandomData("b", period));
        given(stockDetailsDaoEsMock.findByStockTickerOrderByDateDesc("c", pageable)).willReturn(createRandomData("c", period));
        given(stockDetailsDaoEsMock.findByStockTickerOrderByDateDesc("d", pageable)).willReturn(createRandomData("d", 15));
        given(stockDetailsDaoEsMock.findByStockTickerOrderByDateDesc("e", pageable)).willReturn(createRandomData("e", 9));

        //when
        List<CorrelationResult> correlationForA = new ArrayList<>(objectUnderTest.computeCorrelation("a", period,
            correlationType).getBody());

        //then
        assertThat(correlationForA.size()).isEqualTo(2);
    }

    private List<StockDetails> createRandomData(String ticker, int size) {
        List<StockDetails> result = new LinkedList<>();
        Random rnd = new Random();
        LocalDate startDate = LocalDate.of(2016, 5, 20);
        for (int i = 0; i < size; i++) {
            LocalDate date = startDate.minusDays(1);
            result.add(StockDetailsEsBuilder.buildStockDetails()
                .closePrice(new BigDecimal(String.valueOf(rnd.nextInt(500)+1)))
                .date(date)
                .stock(new Stock(ticker, "", ""))
                .build());
            startDate = date;
        }
        return result;
    }
}
