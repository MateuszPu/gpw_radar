package com.gpw.radar.service.technicalIndicators;

import com.gpw.radar.config.CustomDateTimeFormat;
import com.gpw.radar.elasticsearch.stockdetails.StockDetails;
import com.gpw.radar.dao.stockdetails.StockDetailsDAO;
import com.gpw.radar.elasticsearch.stockdetails.dao.StockDetailsEsDAO;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.service.parser.DateAndTimeParserService;
import com.gpw.radar.service.stock.StockService;
import com.gpw.radar.service.technical.indicators.sma.CrossDirection;
import com.gpw.radar.service.technical.indicators.sma.SmaIndicatorService;
import com.gpw.radar.web.rest.dto.stock.StockWithStockIndicatorsDTO;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.mockito.BDDMockito.given;

public class SmaIndicatorServiceTest {

    private StockDetailsDAO stockDetailsDaoEsMock = Mockito.mock(StockDetailsEsDAO.class);
    private StockRepository stockRepositoryMock = Mockito.mock(StockRepository.class);
    private StockService stockServiceMock = Mockito.mock(StockService.class);

    private SmaIndicatorService objectUnderTest = new SmaIndicatorService(stockDetailsDaoEsMock, stockRepositoryMock, stockServiceMock);

    @Test
    public void shouldCorrectReturnResultForSmaCrossover() {
        //given
        given(stockRepositoryMock.findAllTickers()).willReturn(new HashSet<>(Arrays.asList("tpe", "kgh", "pko")));
        given(stockDetailsDaoEsMock.findByStockTickerOrderByDateDesc("kgh", new PageRequest(0, 90)))
            .willReturn(getStockDetailsFromFile(LocalDate.of(2016, 1, 1), "kgh"));
        given(stockDetailsDaoEsMock.findByStockTickerOrderByDateDesc("tpe", new PageRequest(0, 90)))
            .willReturn(getStockDetailsFromFile(LocalDate.of(2015, 11, 14), "tpe"));
        given(stockDetailsDaoEsMock.findByStockTickerOrderByDateDesc("pko", new PageRequest(0, 90)))
            .willReturn(getStockDetailsFromFile(LocalDate.of(2015, 9, 23), "pko"));
        given(stockServiceMock.getAllStocksFetchStockIndicators()).willReturn(getStockIndicatorsDto("kgh", "tpe", "pko"));

        //when
        List<StockWithStockIndicatorsDTO> resultFromAbove = objectUnderTest.getStocksSmaCrossover(CrossDirection.FROM_ABOVE, 15, 30);
        List<StockWithStockIndicatorsDTO> resultFromBelow = objectUnderTest.getStocksSmaCrossover(CrossDirection.FROM_BELOW, 15, 30);

        //then
        assertThat(resultFromAbove.stream().anyMatch(st -> st.getStockTicker().equals("tpe"))).isEqualTo(true);
        assertThat(resultFromAbove.stream().anyMatch(st -> st.getStockTicker().equals("kgh"))).isEqualTo(false);
        assertThat(resultFromAbove.stream().anyMatch(st -> st.getStockTicker().equals("pko"))).isEqualTo(false);
        assertThat(resultFromBelow.stream().anyMatch(st -> st.getStockTicker().equals("pko"))).isEqualTo(true);
        assertThat(resultFromBelow.stream().anyMatch(st -> st.getStockTicker().equals("kgh"))).isEqualTo(false);
        assertThat(resultFromBelow.stream().anyMatch(st -> st.getStockTicker().equals("tpe"))).isEqualTo(false);
    }

    @Test
    public void shouldCorrectReturnResultForPriceCrossSma() {
        //given
        given(stockRepositoryMock.findAllTickers()).willReturn(new HashSet<>(Arrays.asList("tpe", "kgh", "pko")));
        given(stockDetailsDaoEsMock.findByStockTickerOrderByDateDesc("kgh", new PageRequest(0, 90)))
            .willReturn(getStockDetailsFromFile(LocalDate.of(2016, 1, 1), "kgh"));
        given(stockDetailsDaoEsMock.findByStockTickerOrderByDateDesc("tpe", new PageRequest(0, 90)))
            .willReturn(getStockDetailsFromFile(LocalDate.of(2015, 12, 23), "tpe"));
        given(stockDetailsDaoEsMock.findByStockTickerOrderByDateDesc("pko", new PageRequest(0, 90)))
            .willReturn(getStockDetailsFromFile(LocalDate.of(2015, 9, 23), "pko"));
        given(stockServiceMock.getAllStocksFetchStockIndicators()).willReturn(getStockIndicatorsDto("kgh", "tpe", "pko"));

        // LocalDate.of(2015, 12, 23) for tpe
        // LocalDate.of(2015, 9, 23) for pko

        //when
        List<StockWithStockIndicatorsDTO> resultFromAbove = objectUnderTest.getStocksPriceCrossSma(CrossDirection.FROM_BELOW, 15);
        List<StockWithStockIndicatorsDTO> resultFromBelow = objectUnderTest.getStocksPriceCrossSma(CrossDirection.FROM_ABOVE, 15);

        //then
        assertThat(resultFromAbove.stream().anyMatch(st -> st.getStockTicker().equals("tpe"))).isEqualTo(true);
        assertThat(resultFromAbove.stream().anyMatch(st -> st.getStockTicker().equals("pko"))).isEqualTo(false);
        assertThat(resultFromAbove.stream().anyMatch(st -> st.getStockTicker().equals("kgh"))).isEqualTo(false);
        assertThat(resultFromBelow.stream().anyMatch(st -> st.getStockTicker().equals("pko"))).isEqualTo(true);
        assertThat(resultFromBelow.stream().anyMatch(st -> st.getStockTicker().equals("tpe"))).isEqualTo(false);
        assertThat(resultFromBelow.stream().anyMatch(st -> st.getStockTicker().equals("kgh"))).isEqualTo(false);

    }

    private List<StockWithStockIndicatorsDTO> getStockIndicatorsDto(String... tickers) {
        List<StockWithStockIndicatorsDTO> result = new LinkedList<>();
        Arrays.stream(tickers).forEach(e -> result.add(new StockWithStockIndicatorsDTO(e)));
        return result;
    }

    private List<StockDetails> getStockDetailsFromFile(LocalDate date, String ticker) {
        InputStream inputStreamOfStockDetails = getClass().getResourceAsStream("/stocks_data/daily/pl/wse_stocks/kgh.txt");
        List<StockDetails> stockDetailsTwo = parseStockDetails(ticker, inputStreamOfStockDetails);
        return stockDetailsTwo.stream().filter(ti -> ti.getDate().isBefore(date)).collect(Collectors.toList());
    }

    private List<StockDetails> parseStockDetails(String ticker, InputStream inputStreamOfStockDetails) {
        List<StockDetails> result = new ArrayList<>();
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStreamOfStockDetails));
        in.lines().forEach(ln -> result.add(parse(ticker, ln)));
        return result;
    }

    private StockDetails parse(String ticker, String ln) {
        CustomDateTimeFormat customDateTimeFormat = new CustomDateTimeFormat();
        DateAndTimeParserService parser = new DateAndTimeParserService(null,
            customDateTimeFormat.localDateTimeFormatter(), customDateTimeFormat.localTimeFormatter());

        String[] split = ln.split(",");
        StockDetails result = new StockDetails();
        result.setStockWith(ticker, "name", "shortName");
        result.setDate(parser.parseLocalDateFromString(split[0]));
        result.setOpenPrice(new BigDecimal(split[1]));
        result.setMaxPrice(new BigDecimal(split[2]));
        result.setMinPrice(new BigDecimal(split[3]));
        result.setClosePrice(new BigDecimal(split[4]));
        result.setVolume(Long.valueOf(split[5]));
        return result;
    }
}
