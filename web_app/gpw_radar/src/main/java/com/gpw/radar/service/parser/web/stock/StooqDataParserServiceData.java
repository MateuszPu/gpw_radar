package com.gpw.radar.service.parser.web.stock;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Data downloading from stooq.pl using jsoup library
@Service("stooqDataParserService")
public class StooqDataParserServiceData implements StockDataDetailsWebParser {

    private final String regex = "(?<=\\)\\s-)(.*?)(?=-)";

    @Override
    public String getStockNameFromWeb(Document doc) {
        String title = doc.title();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(title);
        String stockName = "";
        if (matcher.find()) {
            stockName = matcher.group(0);
        }
        String stockNameOutOfSpacesAndUpperCase = stockName.trim().toUpperCase();
        return stockNameOutOfSpacesAndUpperCase;
    }

    @Override
    public String getStockShortNameFromWeb(Document doc) {
        Elements links = doc.select("meta");
        Element table = links.get(1);
        String attr = table.attr("content");
        String stockShortName = attr.substring(0, attr.indexOf(","));
        return stockShortName.toUpperCase();
    }

    @Override
    public BigDecimal parseOpenPrice(Document doc) {
        String ticker = parseTicker(doc);
        BigDecimal open = new BigDecimal(doc.getElementById("aq_" + ticker + "_o").ownText());
        return open;
    }

    @Override
    public BigDecimal parseClosePrice(Document doc) {
        String ticker = parseTicker(doc);
        BigDecimal close = new BigDecimal(doc.getElementById("aq_" + ticker + "_c2").ownText());
        return close;
    }

    @Override
    public BigDecimal parseMaxPrice(Document doc) {
        String ticker = parseTicker(doc);
        BigDecimal max = new BigDecimal(doc.getElementById("aq_" + ticker + "_h").ownText());
        return max;
    }

    @Override
    public BigDecimal parseMinPrice(Document doc) {
        String ticker = parseTicker(doc);
        BigDecimal low = new BigDecimal(doc.getElementById("aq_" + ticker + "_l").ownText());
        return low;
    }

    @Override
    public LocalDate parseDate(Document doc) {
        String ticker = parseTicker(doc);
        LocalDate date = LocalDate.parse(doc.getElementById("aq_" + ticker + "_d2").ownText());
        return date;
    }

    @Override
    public Long parseVolume(Document doc) {
        String ticker = parseTicker(doc);
        Long volume = Long.valueOf(doc.getElementById("aq_" + ticker + "_v2").ownText().replace("k", "000"));
        return volume;
    }

    private String parseTicker(Document doc) {
        String title = doc.title();
        String[] split = title.split(" ");
        return split[0].toLowerCase();
    }
}
