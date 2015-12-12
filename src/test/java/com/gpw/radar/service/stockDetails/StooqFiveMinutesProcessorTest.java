package com.gpw.radar.service.stockDetails;


import com.gpw.radar.Application;
import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockFiveMinutesDetails;
import com.gpw.radar.domain.stock.StockFiveMinutesIndicators;
import com.gpw.radar.repository.stock.StockFiveMinutesIndicatorsRepository;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.service.auto.update.stockFiveMinutesDetails.StooqFiveMinutesProcessor;
import org.assertj.core.data.Percentage;
import org.junit.Before;
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
import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class StooqFiveMinutesProcessorTest {

    @Inject
    private StooqFiveMinutesProcessor stooqFiveMinutesProcessor;

    @Inject
    private StockRepository stockRepository;

    @Inject
    private StockFiveMinutesIndicatorsRepository stockFiveMinutesIndicatorsRepository;

    private InputStreamReader inputStreamReader;
    private InputStream inputStreamOfStockFiveMinutesDetails;
    private String stockFiveMinutesDetailsFilePath;
    public static boolean dbInit = false;

    @Before
    public void init() {
        if(!dbInit) {
            initDB();
            dbInit = true;
        }
        stockFiveMinutesDetailsFilePath = "/stocks_data/5min/pl/wse_stocks/detailsOnlineList.prn";
        inputStreamOfStockFiveMinutesDetails = getClass().getResourceAsStream(stockFiveMinutesDetailsFilePath);
        inputStreamReader = new InputStreamReader(inputStreamOfStockFiveMinutesDetails);
    }

    private void initDB() {
        List<Stock> stocks = new ArrayList<>();
        Stock kgh = new Stock();
        kgh.setTicker(StockTicker.kgh);
        Stock tpe = new Stock();
        tpe.setTicker(StockTicker.tpe);
        Stock cdr = new Stock();
        cdr.setTicker(StockTicker.cdr);
        stocks.add(kgh);
        stocks.add(tpe);
        stocks.add(cdr);
        stockRepository.save(stocks);
        initStockFiveMinutesIndicators(stocks);
    }

    private void initStockFiveMinutesIndicators(List<Stock> stocks) {
        List<StockFiveMinutesIndicators> indicators = new ArrayList<>();
        StockFiveMinutesIndicators kghIndicators = new StockFiveMinutesIndicators();
        kghIndicators.setTime(LocalTime.of(9, 5));
        kghIndicators.setAverageVolume(18365);
        kghIndicators.setStock(stocks.stream()
            .filter(st -> st.getTicker().equals(StockTicker.kgh)).findFirst().get());
        indicators.add(kghIndicators);

        StockFiveMinutesIndicators tpeIndicators = new StockFiveMinutesIndicators();
        tpeIndicators.setTime(LocalTime.of(9, 5));
        tpeIndicators.setAverageVolume(34754);
        tpeIndicators.setStock(stocks.stream()
            .filter(st -> st.getTicker().equals(StockTicker.tpe)).findFirst().get());
        indicators.add(tpeIndicators);

        StockFiveMinutesIndicators cdrIndicators = new StockFiveMinutesIndicators();
        cdrIndicators.setTime(LocalTime.of(9, 5));
        cdrIndicators.setAverageVolume(6010);
        cdrIndicators.setStock(stocks.stream()
            .filter(st -> st.getTicker().equals(StockTicker.cdr)).findFirst().get());
        indicators.add(cdrIndicators);

        StockFiveMinutesIndicators kgh2Indicators = new StockFiveMinutesIndicators();
        kgh2Indicators.setTime(LocalTime.of(9, 10));
        kgh2Indicators.setAverageVolume(38365);
        kgh2Indicators.setStock(stocks.stream()
            .filter(st -> st.getTicker().equals(StockTicker.kgh)).findFirst().get());
        indicators.add(kgh2Indicators);

        StockFiveMinutesIndicators tpe2Indicators = new StockFiveMinutesIndicators();
        tpe2Indicators.setTime(LocalTime.of(9, 10));
        tpe2Indicators.setAverageVolume(84754);
        tpe2Indicators.setStock(stocks.stream()
            .filter(st -> st.getTicker().equals(StockTicker.tpe)).findFirst().get());
        indicators.add(tpe2Indicators);

        StockFiveMinutesIndicators cdr2Indicators = new StockFiveMinutesIndicators();
        cdr2Indicators.setTime(LocalTime.of(9, 10));
        cdr2Indicators.setAverageVolume(12010);
        cdr2Indicators.setStock(stocks.stream()
            .filter(st -> st.getTicker().equals(StockTicker.cdr)).findFirst().get());
        indicators.add(cdr2Indicators);
        stockFiveMinutesIndicatorsRepository.save(indicators);
    }

    @Test
    public void stockFiveMinutesDetailsSizeByTime() {
        List<StockFiveMinutesDetails> list = stooqFiveMinutesProcessor.getCurrentFiveMinutesStockDetails(inputStreamReader, LocalTime.of(9, 5));
        assertThat(list.size()).isEqualTo(3);

        InputStream inputStreamNextOne = getClass().getResourceAsStream(stockFiveMinutesDetailsFilePath);
        InputStreamReader inputStreamReaderNextOne = new InputStreamReader(inputStreamNextOne);
        list = stooqFiveMinutesProcessor.getCurrentFiveMinutesStockDetails(inputStreamReaderNextOne, LocalTime.of(9, 10));
        assertThat(list.size()).isEqualTo(2);
    }

    @Test
    public void stockFiveMinutesDetailsFillEmptyData() {
        List<StockFiveMinutesDetails> stockFiveMinutesDetailsAt905 = stooqFiveMinutesProcessor.getCurrentFiveMinutesStockDetails(inputStreamReader, LocalTime.of(9, 5));
        InputStream inputStreamNextOne = getClass().getResourceAsStream(stockFiveMinutesDetailsFilePath);
        InputStreamReader inputStreamReaderNextOne = new InputStreamReader(inputStreamNextOne);
        List<StockFiveMinutesDetails> stockFiveMinutesDetailsAt910 = stooqFiveMinutesProcessor.getCurrentFiveMinutesStockDetails(inputStreamReaderNextOne, LocalTime.of(9, 10));

        assertThat(stockFiveMinutesDetailsAt905.size()).isEqualTo(3);
        assertThat(stockFiveMinutesDetailsAt910.size()).isEqualTo(2);
        stockFiveMinutesDetailsAt910 = stooqFiveMinutesProcessor.fillEmptyTimeWithData(stockFiveMinutesDetailsAt910, stockFiveMinutesDetailsAt905);
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
        List<StockFiveMinutesDetails> stockFiveMinutesDetailsAt905 = stooqFiveMinutesProcessor.getCurrentFiveMinutesStockDetails(inputStreamReader, LocalTime.of(9, 5));
        List<StockFiveMinutesDetails> withCumulatedVolumeAt905 = stooqFiveMinutesProcessor.calculateCumulatedVolume(stockFiveMinutesDetailsAt905, LocalTime.of(9, 5));

        InputStream inputStreamNextOne = getClass().getResourceAsStream(stockFiveMinutesDetailsFilePath);
        InputStreamReader inputStreamReaderNewOne = new InputStreamReader(inputStreamNextOne);
        List<StockFiveMinutesDetails> stockFiveMinutesDetailsAt910 = stooqFiveMinutesProcessor.getCurrentFiveMinutesStockDetails(inputStreamReaderNewOne, LocalTime.of(9, 10));
        List<StockFiveMinutesDetails> withCumulatedVolumeAt910 = stooqFiveMinutesProcessor.calculateCumulatedVolume(stockFiveMinutesDetailsAt910, LocalTime.of(9, 10));
        withCumulatedVolumeAt910 = stooqFiveMinutesProcessor.fillEmptyTimeWithData(withCumulatedVolumeAt910, withCumulatedVolumeAt905);
        assertThat(withCumulatedVolumeAt910.stream()
            .filter(stock -> stock.getStockTicker().equals(StockTicker.tpe))
            .findFirst()
            .get()
            .getCumulatedVolume()).isEqualTo(69754);
        assertThat(withCumulatedVolumeAt910.stream()
            .filter(stock -> stock.getStockTicker().equals(StockTicker.cdr))
            .findFirst()
            .get()
            .getCumulatedVolume()).isEqualTo(21878);
    }

    @Test
    public void stockFiveMinutesDetailsSetStocks() {
        List<StockFiveMinutesDetails> stockFiveMinutesDetailsAt905 = stooqFiveMinutesProcessor.getCurrentFiveMinutesStockDetails(inputStreamReader, LocalTime.of(9, 5));
        stooqFiveMinutesProcessor.setStockToEachDetail(stockFiveMinutesDetailsAt905);
        StockFiveMinutesDetails kghDetails = stockFiveMinutesDetailsAt905.stream()
            .filter(st -> st.getStockTicker().equals(StockTicker.kgh))
            .findFirst()
            .get();
        Stock kghStock = stockRepository.findByTicker(StockTicker.kgh);
        assertThat(kghDetails.getStock()).isEqualTo(kghStock);

    }

    @Test
    public void stockFiveMinutesDetailsCalculateVolumeRatio() {
        List<StockFiveMinutesDetails> stockFiveMinutesDetailsAt905 = stooqFiveMinutesProcessor.getCurrentFiveMinutesStockDetails(inputStreamReader, LocalTime.of(9, 5));
        stockFiveMinutesDetailsAt905 = stooqFiveMinutesProcessor.calculateCumulatedVolume(stockFiveMinutesDetailsAt905, LocalTime.of(9, 5));
        stockFiveMinutesDetailsAt905 = stooqFiveMinutesProcessor.setStockToEachDetail(stockFiveMinutesDetailsAt905);
        stockFiveMinutesDetailsAt905 = stooqFiveMinutesProcessor.calculateVolumeRatio(stockFiveMinutesDetailsAt905, LocalTime.of(9,5));

        StockFiveMinutesDetails cdrDetails = stockFiveMinutesDetailsAt905.stream()
            .filter(dt -> dt.getStockTicker().equals(StockTicker.cdr))
            .findFirst()
            .get();
        assertThat(cdrDetails.getRatioVolume()).isCloseTo(1.3327, Percentage.withPercentage(0.05));

        stooqFiveMinutesProcessor.setLastStockFiveMinuteDetails(stockFiveMinutesDetailsAt905);

        InputStream inputStreamNextOne = getClass().getResourceAsStream(stockFiveMinutesDetailsFilePath);
        InputStreamReader inputStreamReaderNewOne = new InputStreamReader(inputStreamNextOne);
        List<StockFiveMinutesDetails> stockFiveMinutesDetailsAt910 = stooqFiveMinutesProcessor.getCurrentFiveMinutesStockDetails(inputStreamReaderNewOne, LocalTime.of(9, 10));
        stockFiveMinutesDetailsAt910 = stooqFiveMinutesProcessor.fillEmptyTimeWithData(stockFiveMinutesDetailsAt910, stockFiveMinutesDetailsAt905);
        stockFiveMinutesDetailsAt910 = stooqFiveMinutesProcessor.calculateCumulatedVolume(stockFiveMinutesDetailsAt910, LocalTime.of(9, 10));
        stockFiveMinutesDetailsAt910 = stooqFiveMinutesProcessor.setStockToEachDetail(stockFiveMinutesDetailsAt910);
        stockFiveMinutesDetailsAt910 = stooqFiveMinutesProcessor.calculateVolumeRatio(stockFiveMinutesDetailsAt910, LocalTime.of(9,10));

        StockFiveMinutesDetails tpeDetails = stockFiveMinutesDetailsAt910.stream()
            .filter(dt -> dt.getStockTicker().equals(StockTicker.tpe))
            .findFirst()
            .get();
        assertThat(tpeDetails.getRatioVolume()).isCloseTo(0.823017, Percentage.withPercentage(0.05));
    }
}
