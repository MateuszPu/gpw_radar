package com.gpw.radar.service.parser.web.stock;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component("gpwTickerParserService")
public class GpwTickerParserService implements StockTickerParser {

    private static final int indexOfTicker = 3;

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
            tickers.add(select.get(indexOfTicker).text().toLowerCase());
        }
        return tickers;
    }
}
