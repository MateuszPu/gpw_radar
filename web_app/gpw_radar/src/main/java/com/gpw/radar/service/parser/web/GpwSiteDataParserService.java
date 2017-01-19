package com.gpw.radar.service.parser.web;

import com.gpw.radar.service.parser.web.stock.StockBatchWebParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

@Component("gpwSiteDataParserService")
public class GpwSiteDataParserService implements StockBatchWebParser {

    @Inject
    private UrlStreamsGetterService urlStreamsGetterService;

    private final int TICKER_INDEX = 3;
    private final String STOCKS_DATA_URL = "https://www.gpw.pl/ajaxindex.php?action=GPWQuotations&start=showTable&tab=all&lang=PL&full=1";

    @Override
    public Document getDocumentForAllStocks() {
        InputStream inputStreamFromUrl = urlStreamsGetterService.getInputStreamFromUrl(STOCKS_DATA_URL);
        Document doc = getDocumentFromInputStream(inputStreamFromUrl);
        return doc;
    }

    @Override
    public Document getDocumentFromInputStream(InputStream inputStream) {
        String htmlContent = getHtmlContent(inputStream);
        Document doc = Jsoup.parse(htmlContent);
        return doc;
    }

    private String getHtmlContent(InputStream inputStream) {
        String htmlContent = "";

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            htmlContent = bufferedReader.lines().filter(s -> s.startsWith("<table")).findAny().get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return htmlContent;
    }

    @Override
    public Set<String> fetchAllTickers(Document doc) {
        Set<String> tickers = new HashSet<>();
        Elements tableRows = doc.select("tr");

        for (int index = 2; index < tableRows.size() - 1; index++) {

            // skip the table title showing every 20 stock details
            if (index % 22 == 0) {
                index++;
                continue;
            }
            Elements select = tableRows.get(index).select("td");
            tickers.add(select.get(TICKER_INDEX).text().toLowerCase());
        }
        return tickers;
    }
}
