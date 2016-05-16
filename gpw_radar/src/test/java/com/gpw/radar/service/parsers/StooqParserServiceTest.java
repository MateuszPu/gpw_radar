package com.gpw.radar.service.parsers;

import com.gpw.radar.service.parser.web.stock.StooqDataNameParserService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class StooqParserServiceTest {

    private StooqDataNameParserService stooqParserService = new StooqDataNameParserService();

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
}
