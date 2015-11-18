package com.gpw.radar.service;

import com.gpw.radar.Application;
import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.domain.stock.StockFiveMinutesDetails;
import com.gpw.radar.service.database.StockDetailsTextFileParserService;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class StockDetailsTextFileParserServiceTest {

    @Inject
    private StockDetailsTextFileParserService stockDetailsTextFileParserService;

    private List<StockTicker> stockTickerList = new ArrayList<>();
    private Stock stock = new Stock();
    private InputStream inputStreamOfStockDetails;
    private InputStream inputStreamOfStockFiveMinutesDetails;

    @Before
    public void setup() {
        stockTickerList.add(StockTicker.cdr);
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
        List<StockDetails> list = stockDetailsTextFileParserService.parseStockDetailsByStockFromTxtFile(stock, inputStreamOfStockDetails);
        assertTrue("size of the content should be 20", list.size() == 20);
    }

    @Test
    public void stockDetailsContent() {
        List<StockDetails> list = stockDetailsTextFileParserService.parseStockDetailsByStockFromTxtFile(stock, inputStreamOfStockDetails);
        StockDetails firstStockDetails = list.get(0);
        assertTrue("First element in list should have volume = 277939", firstStockDetails.getVolume().equals(277939l));
        assertTrue("First element in list should have open price = 17.9", firstStockDetails.getOpenPrice().equals(new BigDecimal("17.9")));
        assertTrue("First element in list should have max price = 18.38", firstStockDetails.getMaxPrice().equals(new BigDecimal("18.38")));
        assertTrue("First element in list should have min price = 17.81", firstStockDetails.getMinPrice().equals(new BigDecimal("17.81")));
        assertTrue("First element in list should have close price = 17.87", firstStockDetails.getClosePrice().equals(new BigDecimal("17.87")));
        assertTrue("First element in list should have date = 2014-12-03", firstStockDetails.getDate().equals(LocalDate.of(2014,12,03)));
    }

    @Test
    public void stockFiveMinutesDetailsSize() {
        List<StockFiveMinutesDetails> list = stockDetailsTextFileParserService.parseStockFiveMinutesDetailsByStockFromTxtFile(stock, inputStreamOfStockFiveMinutesDetails);
        assertTrue("size of the content should be 85", list.size() == 85);
    }

    @Test
    public void stockFiveMinutesDetailsContent() {
        List<StockFiveMinutesDetails> list = stockDetailsTextFileParserService.parseStockFiveMinutesDetailsByStockFromTxtFile(stock, inputStreamOfStockFiveMinutesDetails);
        StockFiveMinutesDetails stockFiveMinutesDetails = list.get(0);
        assertTrue("First element in list should have volume=1417", stockFiveMinutesDetails.getVolume() == 1417);
        assertTrue("First element in list should have time of 9:05", stockFiveMinutesDetails.getTime().equals(LocalTime.of(9,5)));
        assertTrue("First element in list should have date of 2015-11-10 9:05", stockFiveMinutesDetails.getDate().equals(LocalDateTime.of(2015,11,10,9,5)));
    }

    @Test
    public void stockFiveMinutesDetailsFillEmptyRangeSize() {
        List<StockFiveMinutesDetails> list = stockDetailsTextFileParserService.parseStockFiveMinutesDetailsByStockFromTxtFile(stock, inputStreamOfStockFiveMinutesDetails);
        List<StockFiveMinutesDetails> filledList = stockDetailsTextFileParserService.fillEmptyTimeAndCumulativeVolume(list);
        assertTrue("size of the content should be 94", filledList.size() == 94);
    }

    @Test
    public void stockFiveMinutesDetailsFillEmptyRangeContent() {
        List<StockFiveMinutesDetails> list = stockDetailsTextFileParserService.parseStockFiveMinutesDetailsByStockFromTxtFile(stock, inputStreamOfStockFiveMinutesDetails);
        List<StockFiveMinutesDetails> filledList = stockDetailsTextFileParserService.fillEmptyTimeAndCumulativeVolume(list);
        StockFiveMinutesDetails firstStockFiveMinutesDetails = filledList.get(0);
        StockFiveMinutesDetails thirdStockFiveMinutesDetails = filledList.get(2);
        assertTrue("First element in filled list should have cumulated volume = 1417", firstStockFiveMinutesDetails.getCumulatedVolume() == 1417);
        assertTrue("Third element in filled list should have volume = 0", thirdStockFiveMinutesDetails.getVolume() == 0);
        assertTrue("Third element in filled list should have time = 9:15", thirdStockFiveMinutesDetails.getTime().equals(LocalTime.of(9,15)));
    }
}
