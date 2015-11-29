package com.gpw.radar.service;


import com.gpw.radar.Application;
import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockFiveMinutesDetails;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.service.auto.update.stockFiveMinutesDetails.StooqFiveMinutesParser;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class StooqFiveMinutesParserServiceTest {

    @Inject
    private StooqFiveMinutesParser stooqFiveMinutesParser;

    @Inject
    private StockRepository stockRepository;

    private InputStreamReader inputStreamReader;
    private InputStream inputStreamOfStockFiveMinutesDetails;
    private String stockFiveMinutesDetailsFilePath;

    @Before
    public void init() {
        stockFiveMinutesDetailsFilePath = "/stocks_data/5min/pl/wse_stocks/20151125_5.prn";
        inputStreamOfStockFiveMinutesDetails = getClass().getResourceAsStream(stockFiveMinutesDetailsFilePath);
        inputStreamReader = new InputStreamReader(inputStreamOfStockFiveMinutesDetails);
    }

    @Test
    public void stockFiveMinutesDetailsSizeByTime() {
        List<StockFiveMinutesDetails> list = stooqFiveMinutesParser.getCurrentFiveMinutesStockDetails(inputStreamReader, LocalTime.of(9, 5));
        assertThat(list.size()).isEqualTo(213);
    }

    @Test
    public void stockFiveMinutesDetails() {
        List<StockFiveMinutesDetails> list = stooqFiveMinutesParser.getCurrentFiveMinutesStockDetails(inputStreamReader, LocalTime.of(9, 5));
        List<StockFiveMinutesDetails> listWithCumulatedVolume = stooqFiveMinutesParser.calculateCumulatedVolume(list);
        assertThat(listWithCumulatedVolume.stream().filter(stock -> stock.getStockTicker().equals("abc")).findFirst().get().getVolume()).isEqualTo(100);
        assertThat(listWithCumulatedVolume.stream().filter(stock -> stock.getStockTicker().equals("abc")).findFirst().get().getTime()).isEqualTo(LocalTime.of(9, 5));
    }

    @Test
    public void stockFiveMinutesDetailsCumulatedVolume() {
        List<StockFiveMinutesDetails> list = stooqFiveMinutesParser.getCurrentFiveMinutesStockDetails(inputStreamReader, LocalTime.of(9, 5));
        List<StockFiveMinutesDetails> listWithCumulatedVolume = stooqFiveMinutesParser.calculateCumulatedVolume(list);

        InputStream inputStreamNextOne = getClass().getResourceAsStream(stockFiveMinutesDetailsFilePath);
        InputStreamReader inputStreamReaderNextOne = new InputStreamReader(inputStreamNextOne);
        List<StockFiveMinutesDetails> listNextOne = stooqFiveMinutesParser.getCurrentFiveMinutesStockDetails(inputStreamReaderNextOne, LocalTime.of(9, 10));
        List<StockFiveMinutesDetails> listWithCumulatedVolumeNextOne = stooqFiveMinutesParser.calculateCumulatedVolume(listNextOne);
        assertThat(listWithCumulatedVolumeNextOne.stream().filter(stock -> stock.getStockTicker().equals("abc")).findFirst().get().getCumulatedVolume()).isEqualTo(1100);
        assertThat(listWithCumulatedVolumeNextOne.stream().filter(stock -> stock.getStockTicker().equals("atr")).findFirst().get().getCumulatedVolume()).isEqualTo(5);
    }

    @Ignore(value="true")
    @Test
    public void filteredStockFiveMinutesDetails() {
        List<StockTicker> tickers = new ArrayList<>(Arrays.asList(StockTicker.values()));
        tickers.forEach(stockTicker -> stockRepository.save(new Stock(stockTicker)));

        List<StockFiveMinutesDetails> list = stooqFiveMinutesParser.getCurrentFiveMinutesStockDetails(inputStreamReader, LocalTime.of(9, 5));
        List<StockFiveMinutesDetails> filteredList = stooqFiveMinutesParser.filterDetailsOfStockInApp(list);
        assertThat(filteredList.size()).isEqualTo(2);
    }
}
