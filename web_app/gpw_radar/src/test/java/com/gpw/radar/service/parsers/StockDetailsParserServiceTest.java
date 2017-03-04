package com.gpw.radar.service.parsers;

import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.elasticsearch.stockdetails.StockDetails;
import com.gpw.radar.service.builders.StockBuilder;
import com.gpw.radar.service.parser.DateAndTimeParserService;
import com.gpw.radar.service.parser.web.UrlStreamsGetterService;
import com.gpw.radar.service.parser.web.stockDetails.GpwSiteStockDetailsParser;
import com.gpw.radar.service.parser.web.stockDetails.StockDetailsParser;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;

public class StockDetailsParserServiceTest {

    private final String testStockDetailsPath = "/stocks_data/daily/pl/wse_stocks/2016-07-08_akcje.xls";
    private UrlStreamsGetterService urlStreamsGetterServiceMock = Mockito.mock(UrlStreamsGetterService.class);
    private DateAndTimeParserService dateAndTimeParserServiceMock = Mockito.mock(DateAndTimeParserService.class);
    private StockDetailsParser objectUnderTest = new GpwSiteStockDetailsParser(dateAndTimeParserServiceMock,
        urlStreamsGetterServiceMock, mockStocksInDb());

    private List<Stock> mockStocksInDb() {
        List<Stock> mockedStocksInDb = new ArrayList<>();
        Stock kghm = StockBuilder.buildStock().ticker("kgh").stockShortName("KGHM").stockName("KGHM MIEDZ").build();
        Stock abadonre = StockBuilder.buildStock().ticker("aba").stockShortName("ABADONRE").stockName("Abadon Real Estate SA").build();
        mockedStocksInDb.add(kghm);
        mockedStocksInDb.add(abadonre);
        return mockedStocksInDb;
    }

    @Test
    public void shouldParseQuotedStockDetails() throws IOException {
        //given
        LocalDate exampleDate = LocalDate.of(2016, 7, 8);
        given(dateAndTimeParserServiceMock.parseLocalDateFromString(anyString())).willReturn(exampleDate);
        InputStream inputStreamOfStockDetails = getClass().getResourceAsStream(testStockDetailsPath);
        given(urlStreamsGetterServiceMock.getInputStreamFromUrl(anyObject())).willReturn(inputStreamOfStockDetails);

        //when
        List<StockDetails> details = objectUnderTest.parseStockDetails(exampleDate);

        //then
        StockDetails KghmStockDetails = details.stream().filter(dt -> dt.getStock().getShortName().equals("KGHM")).findAny().get();
        assertThat(KghmStockDetails.getDate()).isEqualTo(exampleDate);
        assertThat(KghmStockDetails.getVolume()).isEqualTo(1016818);
        assertThat(KghmStockDetails.getOpenPrice()).isEqualTo(new BigDecimal("68.00"));
        assertThat(KghmStockDetails.getMaxPrice()).isEqualTo(new BigDecimal("68.60"));
        assertThat(KghmStockDetails.getMinPrice()).isEqualTo(new BigDecimal("67.68"));
        assertThat(KghmStockDetails.getClosePrice()).isEqualTo(new BigDecimal("68.01"));
        assertThat(KghmStockDetails.getStock().getTicker()).isEqualTo("kgh");
        assertThat(KghmStockDetails.getStock().getShortName()).isEqualTo("KGHM");
        assertThat(KghmStockDetails.getStock().getName()).isEqualTo("KGHM MIEDZ");
        assertThat(KghmStockDetails.getTransactionsNumber()).isEqualTo(5841);
    }

    @Test
    public void shouldParseNotQuotedStockDetails() throws IOException {
        //given
        LocalDate exampleDate = LocalDate.of(2016, 7, 8);
        given(dateAndTimeParserServiceMock.parseLocalDateFromString(anyString())).willReturn(exampleDate);
        InputStream inputStreamOfStockDetails = getClass().getResourceAsStream(testStockDetailsPath);
        given(urlStreamsGetterServiceMock.getInputStreamFromUrl(anyObject())).willReturn(inputStreamOfStockDetails);

        //when
        List<StockDetails> details = objectUnderTest.parseStockDetails(exampleDate);

        //then
        StockDetails AbadonreStockDetails = details.stream().filter(dt -> dt.getStock().getShortName().equals("ABADONRE")).findAny().get();
        assertThat(AbadonreStockDetails.getDate()).isEqualTo(exampleDate);
        assertThat(AbadonreStockDetails.getVolume()).isEqualTo(0);
        assertThat(AbadonreStockDetails.getOpenPrice()).isEqualTo(new BigDecimal("1.68"));
        assertThat(AbadonreStockDetails.getMaxPrice()).isEqualTo(new BigDecimal("1.68"));
        assertThat(AbadonreStockDetails.getMinPrice()).isEqualTo(new BigDecimal("1.68"));
        assertThat(AbadonreStockDetails.getClosePrice()).isEqualTo(new BigDecimal("1.68"));
        assertThat(AbadonreStockDetails.getTransactionsNumber()).isEqualTo(0);
    }
}
