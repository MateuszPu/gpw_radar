package com.gpw.radar.service.parsers;

import com.gpw.radar.service.parser.web.stock.StooqDataParserService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class StooqParserServiceTest {

    private StooqDataParserService stooqParserService = new StooqDataParserService();

    private Document htmlDoc;

    @Before
    public void init() throws IOException {
        String htmlStooqSite = "/stocks_data/stooqSite.html";
        try (InputStream in = getClass().getResourceAsStream(htmlStooqSite)) {
            htmlDoc = Jsoup.parse(in, null, "uri cannot be null");
        }
    }

    @Test
    public void getStockNameTest() {
        String stockName = stooqParserService.getStockNameFromWeb(htmlDoc);
        String expectedResult = "KGHM Polska Miedź SA";
        assertThat(stockName).isEqualTo(expectedResult.toUpperCase());
    }

    @Test
    public void getStockShortNameTest() {
        String stockName = stooqParserService.getStockShortNameFromWeb(htmlDoc);
        String expectedResult = "KGHM";
        assertThat(stockName).isEqualTo(expectedResult.toUpperCase());
    }

    @Test
    public void getOpenPriceTest() {
        BigDecimal open = stooqParserService.parseOpenPrice(htmlDoc);
        BigDecimal expectedResult = new BigDecimal("60.67");
        assertThat(open).isEqualTo(expectedResult);
    }

    @Test
    public void getClosePriceTest() {
        BigDecimal close = stooqParserService.parseClosePrice(htmlDoc);
        BigDecimal expectedResult = new BigDecimal("59.85");
        assertThat(close).isEqualTo(expectedResult);
    }

    @Test
    public void getMaxPriceTest() {
        BigDecimal max = stooqParserService.parseMaxPrice(htmlDoc);
        BigDecimal expectedResult = new BigDecimal("60.90");
        assertThat(max).isEqualTo(expectedResult);
    }

    @Test
    public void getMinPriceTest() {
        BigDecimal min = stooqParserService.parseMinPrice(htmlDoc);
        BigDecimal expectedResult = new BigDecimal("59.21");
        assertThat(min).isEqualTo(expectedResult);
    }

    @Test
    public void getDataTest() {
        LocalDate date = stooqParserService.parseDate(htmlDoc);
        LocalDate expectedResult = LocalDate.of(2016, 6, 10);
        assertThat(date).isEqualTo(expectedResult);
    }
}
