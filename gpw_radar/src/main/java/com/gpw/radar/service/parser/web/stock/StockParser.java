package com.gpw.radar.service.parser.web.stock;


import org.jsoup.nodes.Document;

public interface StockParser {

    String getStockNameFromWeb(Document doc);
    String getStockShortNameFromWeb(Document doc);
}
