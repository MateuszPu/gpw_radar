package com.gpw.radar.service.parser.web.stock;

import org.jsoup.nodes.Document;

import java.util.Set;

public interface StockTickerParser {
    Set<String> fetchAllTickers(Document doc);
}
