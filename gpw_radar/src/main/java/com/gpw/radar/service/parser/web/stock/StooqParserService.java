package com.gpw.radar.service.parser.web.stock;

import com.gpw.radar.domain.stock.Stock;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

//Data downloading from stooq.pl using jsoup library
@Service
public class StooqParserService implements StockParser {

    private final Logger logger = LoggerFactory.getLogger(StooqParserService.class);

    public String getStockNameFromWeb(Document doc) {
        String title = doc.title();
        String stockName = title.substring(title.indexOf("- ") + 2, title.indexOf("- Stooq"));
        String stockNameOutOfSpacesAndUpperCase = stockName.trim().toUpperCase();
        return stockNameOutOfSpacesAndUpperCase;
    }

    public String getStockShortNameFromWeb(Document doc) {
        Elements links = doc.select("meta");
        Element table = links.get(1);
        String attr = table.attr("content");
        String stockShortName = attr.substring(0, attr.indexOf(","));
        return stockShortName;
    }
}
