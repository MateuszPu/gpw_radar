package com.gpw.radar.service.parser.web.stock;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Data downloading from stooq.pl using jsoup library
@Service
public class StooqDataParserService implements StockDataParser {

    private final Logger logger = LoggerFactory.getLogger(StooqDataParserService.class);
    private final String regex = "(?<=\\)\\s-)(.*?)(?=-)";

    public String getStockNameFromWeb(Document doc) {
        String title = doc.title();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(title);
        String stockName ="";
        if (matcher.find())
        {
            stockName = matcher.group(0);
        }
        String stockNameOutOfSpacesAndUpperCase = stockName.trim().toUpperCase();
        return stockNameOutOfSpacesAndUpperCase;
    }

    public String getStockShortNameFromWeb(Document doc) {
        Elements links = doc.select("meta");
        Element table = links.get(1);
        String attr = table.attr("content");
        String stockShortName = attr.substring(0, attr.indexOf(","));
        return stockShortName.toUpperCase();
    }
}
