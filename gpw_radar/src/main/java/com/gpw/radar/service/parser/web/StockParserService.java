package com.gpw.radar.service.parser.web;

import com.gpw.radar.domain.stock.Stock;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class StockParserService {

    private final Logger logger = LoggerFactory.getLogger(StockParserService.class);

    public Stock setNameAndShortName(Stock stock) {
        Document doc = null;
        try {
            doc = getDocumentFromStooqWeb(stock.getTicker().toString());
        } catch (IOException e) {
            logger.error("Error occurs: " + e.getMessage());
        }

        String stockName = getStockNameFromWeb(doc);
        stock.setStockName(stockName);

        String stockShortName = getStockShortNameFromWeb(doc);
        stock.setStockShortName(stockShortName);

        return stock;
    }

    private String getStockNameFromWeb(Document doc) {
        String title = doc.title();
        String stockName = title.substring(title.indexOf("- ") + 2, title.indexOf("- Stooq"));
        String stockNameOutOfSpacesAndUpperCase = stockName.trim().toUpperCase();
        return stockNameOutOfSpacesAndUpperCase;
    }

    private String getStockShortNameFromWeb(Document doc) {
        Elements links = doc.select("meta");
        Element table = links.get(1);
        String attr = table.attr("content");
        String stockShortName = attr.substring(0, attr.indexOf(","));
        return stockShortName;
    }

    private Document getDocumentFromStooqWeb(String ticker) throws IOException {
        Document doc = Jsoup.connect("http://stooq.pl/q/?s=" + ticker).get();
        return doc;
    }
}
