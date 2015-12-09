package com.gpw.radar.service.stockDetails;


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
public class StooqFiveMinutesWebParserCronServiceTest {

    @Inject
    private StooqFiveMinutesParser stooqFiveMinutesParser;

    @Inject
    private StockRepository stockRepository;

    private InputStreamReader inputStreamReader;
    private InputStream inputStreamOfStockFiveMinutesDetails;
    private String stockFiveMinutesDetailsFilePath;

    @Before
    public void init() {
        stockFiveMinutesDetailsFilePath = "/stocks_data/5min/pl/wse_stocks/detailsOnlineList.prn";
        inputStreamOfStockFiveMinutesDetails = getClass().getResourceAsStream(stockFiveMinutesDetailsFilePath);
        inputStreamReader = new InputStreamReader(inputStreamOfStockFiveMinutesDetails);
    }

    @Test
    public void stockFiveMinutesDetailsSizeByTime() {
        List<StockFiveMinutesDetails> list = stooqFiveMinutesParser.getCurrentFiveMinutesStockDetails(inputStreamReader, LocalTime.of(9, 5));
        assertThat(list.size()).isEqualTo(3);
    }

    @Test
    public void stockFiveMinutesDetails() {
        List<StockFiveMinutesDetails> stockFiveMinutesDetailsAt905 = stooqFiveMinutesParser.getCurrentFiveMinutesStockDetails(inputStreamReader, LocalTime.of(9, 5));
        InputStream inputStreamNextOne = getClass().getResourceAsStream(stockFiveMinutesDetailsFilePath);
        InputStreamReader inputStreamReaderNextOne = new InputStreamReader(inputStreamNextOne);
        List<StockFiveMinutesDetails> stockFiveMinutesDetailsAt910 = stooqFiveMinutesParser.getCurrentFiveMinutesStockDetails(inputStreamReaderNextOne, LocalTime.of(9, 10));

        assertThat(stockFiveMinutesDetailsAt905.size()).isEqualTo(3);
        assertThat(stockFiveMinutesDetailsAt910.size()).isEqualTo(2);
        stockFiveMinutesDetailsAt910 = stooqFiveMinutesParser.fillEmptyTimeWithData(stockFiveMinutesDetailsAt910, stockFiveMinutesDetailsAt905);
        assertThat(stockFiveMinutesDetailsAt910.size()).isEqualTo(3);
        StockFiveMinutesDetails missingStockFiveMinutesDetails = stockFiveMinutesDetailsAt910.stream()
                .filter(stock -> stock.getStockTicker().equals(StockTicker.tpe))
                .filter(stock -> stock.getTime().equals(LocalTime.of(9, 10)))
                .findAny()
                .get();
        assertThat(missingStockFiveMinutesDetails.getVolume()).isEqualTo(0);
    }

    @Test
    public void stockFiveMinutesDetailsCumulatedVolume() {
        List<StockFiveMinutesDetails> stockFiveMinutesDetailsAt905 = stooqFiveMinutesParser.getCurrentFiveMinutesStockDetails(inputStreamReader, LocalTime.of(9, 5));
        List<StockFiveMinutesDetails> withCumulatedVolumeAt905 = stooqFiveMinutesParser.calculateCumulatedVolume(stockFiveMinutesDetailsAt905, LocalTime.of(9, 5));

        InputStream inputStreamNextOne = getClass().getResourceAsStream(stockFiveMinutesDetailsFilePath);
        InputStreamReader inputStreamReaderNewOne = new InputStreamReader(inputStreamNextOne);
        List<StockFiveMinutesDetails> stockFiveMinutesDetailsAt910 = stooqFiveMinutesParser.getCurrentFiveMinutesStockDetails(inputStreamReaderNewOne, LocalTime.of(9, 10));
        List<StockFiveMinutesDetails> withCumulatedVolumeAt910 = stooqFiveMinutesParser.calculateCumulatedVolume(stockFiveMinutesDetailsAt910, LocalTime.of(9, 10));
        withCumulatedVolumeAt910 = stooqFiveMinutesParser.fillEmptyTimeWithData(withCumulatedVolumeAt910, withCumulatedVolumeAt905);
        assertThat(withCumulatedVolumeAt910.stream().filter(stock -> stock.getStockTicker().equals(StockTicker.tpe)).findFirst().get().getCumulatedVolume()).isEqualTo(69754);
        assertThat(withCumulatedVolumeAt910.stream().filter(stock -> stock.getStockTicker().equals(StockTicker.cdr)).findFirst().get().getCumulatedVolume()).isEqualTo(21878);
    }
}
