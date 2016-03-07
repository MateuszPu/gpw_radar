package com.gpw.radar.service.stockDetails;

import com.gpw.radar.Application;
import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.domain.stock.StockFiveMinutesDetails;
import com.gpw.radar.service.builders.StockBuilder;
import com.gpw.radar.service.parser.file.stockDetails.FileStockDetailsParserService;
import com.gpw.radar.service.parser.file.stockFiveMinutesDetails.FileStockFiveMinutesDetailsParserService;
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
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class FileStockDetailsParserServiceTest {

    @Inject
    private FileStockDetailsParserService fileStockDetailsParserService;

    @Inject
    private FileStockFiveMinutesDetailsParserService fileStockFiveMinutesDetailsParserService;

    private Stock stock;
    private InputStream inputStreamOfStockDetails;
    private InputStream inputStreamOfStockFiveMinutesDetails;

    @Before
    public void init() {
        stock = StockBuilder.sampleStock().ticker(StockTicker.cdr).stockName("cdr").stockShortName("CDR").build();
        String stockDetailsFilePath = "/stocks_data/daily/pl/wse_stocks/" + stock.getTicker().name() + ".txt";
        String stockFiveMinutesDetailsFilePath = "/stocks_data/5min/pl/wse_stocks/" + stock.getTicker().name() + ".txt";
        inputStreamOfStockDetails = getClass().getResourceAsStream(stockDetailsFilePath);
        inputStreamOfStockFiveMinutesDetails = getClass().getResourceAsStream(stockFiveMinutesDetailsFilePath);
    }

    @Test
    public void stockDetailsSize() {
        List<StockDetails> list = fileStockDetailsParserService.parseStockDetails(stock, inputStreamOfStockDetails);
        assertThat(list.size()).isEqualTo(20);
    }

    @Test
    public void stockDetailsContent() {
        List<StockDetails> list = fileStockDetailsParserService.parseStockDetails(stock, inputStreamOfStockDetails);
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
        List<StockFiveMinutesDetails> list = fileStockFiveMinutesDetailsParserService.parseStockFiveMinutesDetails(stock, inputStreamOfStockFiveMinutesDetails);
        assertThat(list.size()).isEqualTo(85);
    }

    @Test
    public void stockFiveMinutesDetailsContent() {
        List<StockFiveMinutesDetails> list = fileStockFiveMinutesDetailsParserService.parseStockFiveMinutesDetails(stock, inputStreamOfStockFiveMinutesDetails);
        StockFiveMinutesDetails stockFiveMinutesDetails = list.get(0);
        assertThat(stockFiveMinutesDetails.getVolume()).isEqualTo(1417);
        assertThat(stockFiveMinutesDetails.getTime()).isEqualTo(LocalTime.of(9,5));
        assertThat(stockFiveMinutesDetails.getDate()).isEqualTo(LocalDate.of(2015,11,10));
        assertThat(stockFiveMinutesDetails.getStock()).isNotNull();
    }

    @Test
    public void stockFiveMinutesDetailsFillEmptyRangeSize() {
        List<StockFiveMinutesDetails> list = fileStockFiveMinutesDetailsParserService.parseStockFiveMinutesDetails(stock, inputStreamOfStockFiveMinutesDetails);
        List<StockFiveMinutesDetails> filledList = fileStockFiveMinutesDetailsParserService.fillEmptyTimeAndCumulativeVolume(list);
        assertThat(filledList.size()).isEqualTo(94);
    }

    @Test
    public void stockFiveMinutesDetailsFillEmptyRangeContent() {
        List<StockFiveMinutesDetails> list = fileStockFiveMinutesDetailsParserService.parseStockFiveMinutesDetails(stock, inputStreamOfStockFiveMinutesDetails);
        List<StockFiveMinutesDetails> filledList = fileStockFiveMinutesDetailsParserService.fillEmptyTimeAndCumulativeVolume(list);
        StockFiveMinutesDetails firstStockFiveMinutesDetails = filledList.stream().findFirst().get();
        StockFiveMinutesDetails thirdStockFiveMinutesDetails = filledList.get(2);
        StockFiveMinutesDetails stockFiveMinutesDetails = filledList.stream()
            .filter(dt -> dt.getTime().equals(LocalTime.of(10,15)))
            .findFirst()
            .get();
        assertThat(firstStockFiveMinutesDetails.getCumulatedVolume()).isEqualTo(1417);
        assertThat(thirdStockFiveMinutesDetails.getVolume()).isEqualTo(0);
        assertThat(thirdStockFiveMinutesDetails.getTime()).isEqualTo(LocalTime.of(9,15));
        assertThat(stockFiveMinutesDetails.getCumulatedVolume()).isEqualTo(20359);
        assertThat(stockFiveMinutesDetails.getVolume()).isEqualTo(0);
    }
}
