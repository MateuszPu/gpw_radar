package com.gpw.radar.service.parsers;

import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.service.builders.StockBuilder;
import com.gpw.radar.service.parser.DateAndTimeParserService;
import com.gpw.radar.service.parser.web.UrlStreamsGetterService;
import com.gpw.radar.service.parser.web.stockDetails.StockDetailsParser;
import com.gpw.radar.service.parser.web.stockDetails.StockDetailsParserImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

public class StockDetailsParserServiceTest {

    private StockDetailsParser stockDetailsParserImpl;
    private UrlStreamsGetterService mockedUrlStreamsGetterService;

    @Before
    public void init() {
        mockUrlStreamsGetterService();
        DateAndTimeParserService dateAndTimeParserService = new DateAndTimeParserService(null);
        dateAndTimeParserService.init();
        stockDetailsParserImpl = new StockDetailsParserImpl(dateAndTimeParserService,
            mockedUrlStreamsGetterService,
            getStocks());
    }

    private void mockUrlStreamsGetterService() {
        String exampleDataPath = "/stocks_data/daily/pl/wse_stocks/2016-07-08_akcje.xls";
        InputStream inputStreamOfStockDetails = getClass().getResourceAsStream(exampleDataPath);
        mockedUrlStreamsGetterService = Mockito.mock(UrlStreamsGetterService.class);
        when(mockedUrlStreamsGetterService.getInputStreamFromUrl(anyObject())).thenReturn(inputStreamOfStockDetails);
    }

    private List<Stock> getStocks() {
        List<Stock> mockedStocksInDb = new ArrayList<>();
        Stock st = StockBuilder.sampleStock().stockShortName("KGHM").build();
        mockedStocksInDb.add(st);
        return mockedStocksInDb;
    }

    @Test
    public void getStockDetailsTest() throws IOException {
        LocalDate testDate = LocalDate.of(2016, 7, 8);
        List<StockDetails> details = stockDetailsParserImpl.parseStockDetails(testDate);
        StockDetails stockDetails = details.stream().filter(dt -> dt.getStock().getStockShortName().equals("KGHM")).findAny().get();

        assertThat(stockDetails.getDate()).isEqualTo(testDate);
        assertThat(stockDetails.getVolume()).isEqualTo(1016818);
        assertThat(stockDetails.getOpenPrice()).isEqualTo(new BigDecimal("68.00"));
        assertThat(stockDetails.getMaxPrice()).isEqualTo(new BigDecimal("68.60"));
        assertThat(stockDetails.getMinPrice()).isEqualTo(new BigDecimal("67.68"));
        assertThat(stockDetails.getClosePrice()).isEqualTo(new BigDecimal("68.01"));
        assertThat(stockDetails.getTransactionsNumber()).isEqualTo(5841);
    }
}
