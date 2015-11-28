package com.gpw.radar.service;

import com.gpw.radar.Application;
import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.domain.stock.StockFiveMinutesDetails;
import com.gpw.radar.service.parser.file.StockDetailsParserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class CurrentStockDetailsParserServiceTest {

    @Inject
    private StockDetailsParserService stockDetailsParserService;

    private Stock stock = new Stock();
    private InputStream inputStreamOfStockDetails;
    private InputStream inputStreamOfStockFiveMinutesDetails;

    @Before
    public void init() {
        stock.setTicker(StockTicker.cdr);
        stock.setStockName("CDR");
        stock.setStockShortName("CDR");
        String stockDetailsFilePath = "/stocks_data/daily/pl/wse_stocks/" + stock.getTicker().name() + ".txt";
        String stockFiveMinutesDetailsFilePath = "/stocks_data/5min/pl/wse_stocks/" + stock.getTicker().name() + ".txt";
        inputStreamOfStockDetails = getClass().getResourceAsStream(stockDetailsFilePath);
        inputStreamOfStockFiveMinutesDetails = getClass().getResourceAsStream(stockFiveMinutesDetailsFilePath);
    }

    @Test
    public void stockDetailsSize() {
        List<StockDetails> list = stockDetailsParserService.parseStockDetails(stock, inputStreamOfStockDetails);
        assertThat(list.size()).isEqualTo(20);
    }

    @Test
    public void stockDetailsContent() {
        List<StockDetails> list = stockDetailsParserService.parseStockDetails(stock, inputStreamOfStockDetails);
        StockDetails firstStockDetails = list.get(0);
        assertThat(firstStockDetails.getVolume()).isEqualTo(277939);
        assertThat(firstStockDetails.getOpenPrice()).isEqualTo(new BigDecimal("17.9"));
        assertThat(firstStockDetails.getMaxPrice()).isEqualTo(new BigDecimal("18.38"));
        assertThat(firstStockDetails.getMinPrice()).isEqualTo(new BigDecimal("17.81"));
        assertThat(firstStockDetails.getClosePrice()).isEqualTo(new BigDecimal("17.87"));
        assertThat(firstStockDetails.getDate()).isEqualTo(LocalDate.of(2014,12,03));
        assertThat(firstStockDetails.getStock()).isNotNull();
    }

    @Test
    public void stockFiveMinutesDetailsSize() {
        List<StockFiveMinutesDetails> list = stockDetailsParserService.parseStockFiveMinutesDetails(stock, inputStreamOfStockFiveMinutesDetails);
        assertThat(list.size()).isEqualTo(85);
    }

    @Test
    public void stockFiveMinutesDetailsContent() {
        List<StockFiveMinutesDetails> list = stockDetailsParserService.parseStockFiveMinutesDetails(stock, inputStreamOfStockFiveMinutesDetails);
        StockFiveMinutesDetails stockFiveMinutesDetails = list.get(0);
        assertThat(stockFiveMinutesDetails.getVolume()).isEqualTo(1417);
        assertThat(stockFiveMinutesDetails.getTime()).isEqualTo(LocalTime.of(9,5));
        assertThat(stockFiveMinutesDetails.getDate()).isEqualTo(LocalDateTime.of(2015,11,10,9,5));
    }

    @Test
    public void stockFiveMinutesDetailsFillEmptyRangeSize() {
        List<StockFiveMinutesDetails> list = stockDetailsParserService.parseStockFiveMinutesDetails(stock, inputStreamOfStockFiveMinutesDetails);
        List<StockFiveMinutesDetails> filledList = stockDetailsParserService.fillEmptyTimeAndCumulativeVolume(list);
        assertThat(filledList.size()).isEqualTo(94);
    }

    @Test
    public void stockFiveMinutesDetailsFillEmptyRangeContent() {
        List<StockFiveMinutesDetails> list = stockDetailsParserService.parseStockFiveMinutesDetails(stock, inputStreamOfStockFiveMinutesDetails);
        List<StockFiveMinutesDetails> filledList = stockDetailsParserService.fillEmptyTimeAndCumulativeVolume(list);
        StockFiveMinutesDetails firstStockFiveMinutesDetails = filledList.get(0);
        StockFiveMinutesDetails thirdStockFiveMinutesDetails = filledList.get(2);
        assertThat(firstStockFiveMinutesDetails.getCumulatedVolume()).isEqualTo(1417);
        assertThat(thirdStockFiveMinutesDetails.getVolume()).isEqualTo(0);
        assertThat(thirdStockFiveMinutesDetails.getTime()).isEqualTo(LocalTime.of(9,15));
    }
}
