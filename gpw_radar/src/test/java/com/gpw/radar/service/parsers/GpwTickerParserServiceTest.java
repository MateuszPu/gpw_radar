package com.gpw.radar.service.parsers;

import com.gpw.radar.service.parser.web.stock.GpwTickerParserService;
import com.gpw.radar.service.parser.web.stock.StockTickerParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

import static org.assertj.core.api.StrictAssertions.assertThat;


public class GpwTickerParserServiceTest {

    private StockTickerParser stockTickerParser = new GpwTickerParserService();
    private Document doc;


    @Before
    public void prepareHtmlFile() {
        String exampleDataPath = "/stocks_data/daily/pl/wse_stocks/daily_gpw_site.html";
        InputStream inputStreamOfStockDetails = getClass().getResourceAsStream(exampleDataPath);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStreamOfStockDetails));
        StringBuilder sb = new StringBuilder();
        bufferedReader.lines().forEach(line -> sb.append(line));
        doc = Jsoup.parse(sb.toString());

        try {
            inputStreamOfStockDetails.close();
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void tickerParserResult() throws IOException {
        Set<String> tickers = stockTickerParser.fetchAllTickers(doc);
        assertThat(tickers.contains("abc")).isEqualTo(true);
        assertThat(tickers.contains("fgt")).isEqualTo(true);
        assertThat(tickers.contains("fro")).isEqualTo(true);
        assertThat(tickers.contains("bzw")).isEqualTo(true);
        assertThat(tickers.contains("bst")).isEqualTo(true);
        assertThat(tickers.size()).isEqualTo(139);
    }
}
