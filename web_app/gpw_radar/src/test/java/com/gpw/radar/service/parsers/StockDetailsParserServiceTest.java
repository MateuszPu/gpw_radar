package com.gpw.radar.service.parsers;

import com.gpw.radar.config.CustomDateTimeFormat;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.service.builders.StockBuilder;
import com.gpw.radar.service.parser.DateAndTimeParserService;
import com.gpw.radar.service.parser.web.UrlStreamsGetterService;
import com.gpw.radar.service.parser.web.stockDetails.GpwSiteStockDetailsParser;
import com.gpw.radar.service.parser.web.stockDetails.StockDetailsParser;
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
        CustomDateTimeFormat customDateTimeFormat = new CustomDateTimeFormat();
        mockUrlStreamsGetterService();
        DateAndTimeParserService dateAndTimeParserService = new DateAndTimeParserService(null,
            customDateTimeFormat.localDateTimeFormatter(), customDateTimeFormat.localTimeFormatter());
        stockDetailsParserImpl = new GpwSiteStockDetailsParser(dateAndTimeParserService,
            mockedUrlStreamsGetterService, getStocksInDb());
    }

    private void mockUrlStreamsGetterService() {
        String exampleDataPath = "/stocks_data/daily/pl/wse_stocks/2016-07-08_akcje.xls";
        InputStream inputStreamOfStockDetails = getClass().getResourceAsStream(exampleDataPath);
        mockedUrlStreamsGetterService = Mockito.mock(UrlStreamsGetterService.class);
        when(mockedUrlStreamsGetterService.getInputStreamFromUrl(anyObject())).thenReturn(inputStreamOfStockDetails);
    }

    private List<Stock> getStocksInDb() {
        List<Stock> mockedStocksInDb = new ArrayList<>();
        Stock kghm = StockBuilder.sampleStock().stockShortName("KGHM").build();
        Stock abadonre = StockBuilder.sampleStock().stockShortName("ABADONRE").build();
        mockedStocksInDb.add(kghm);
        mockedStocksInDb.add(abadonre);
        return mockedStocksInDb;
    }


    @Test
    public void parseQutedStockDetails() throws IOException {
        LocalDate testDate = LocalDate.of(2016, 7, 8);
        List<StockDetails> details = stockDetailsParserImpl.parseStockDetails(testDate);
        StockDetails KghmStockDetails = details.stream().filter(dt -> dt.getStock().getStockShortName().equals("KGHM")).findAny().get();

        assertThat(KghmStockDetails.getDate()).isEqualTo(testDate);
        assertThat(KghmStockDetails.getVolume()).isEqualTo(1016818);
        assertThat(KghmStockDetails.getOpenPrice()).isEqualTo(new BigDecimal("68.00"));
        assertThat(KghmStockDetails.getMaxPrice()).isEqualTo(new BigDecimal("68.60"));
        assertThat(KghmStockDetails.getMinPrice()).isEqualTo(new BigDecimal("67.68"));
        assertThat(KghmStockDetails.getClosePrice()).isEqualTo(new BigDecimal("68.01"));
        assertThat(KghmStockDetails.getTransactionsNumber()).isEqualTo(5841);
    }

    @Test
    public void parseNotQutedStockDetails() throws IOException {
        LocalDate testDate = LocalDate.of(2016, 7, 8);
        List<StockDetails> details = stockDetailsParserImpl.parseStockDetails(testDate);

        StockDetails AbadonreStockDetails = details.stream().filter(dt -> dt.getStock().getStockShortName().equals("ABADONRE")).findAny().get();
        assertThat(AbadonreStockDetails.getDate()).isEqualTo(testDate);
        assertThat(AbadonreStockDetails.getVolume()).isEqualTo(0);
        assertThat(AbadonreStockDetails.getOpenPrice()).isEqualTo(new BigDecimal("1.68"));
        assertThat(AbadonreStockDetails.getMaxPrice()).isEqualTo(new BigDecimal("1.68"));
        assertThat(AbadonreStockDetails.getMinPrice()).isEqualTo(new BigDecimal("1.68"));
        assertThat(AbadonreStockDetails.getClosePrice()).isEqualTo(new BigDecimal("1.68"));
        assertThat(AbadonreStockDetails.getTransactionsNumber()).isEqualTo(0);
    }
}
