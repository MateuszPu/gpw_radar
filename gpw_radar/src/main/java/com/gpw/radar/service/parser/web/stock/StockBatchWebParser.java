package com.gpw.radar.service.parser.web.stock;

import org.jsoup.nodes.Document;

import java.io.InputStream;
import java.util.Set;

public interface StockBatchWebParser {
    Set<String> fetchAllTickers(Document doc);
    Document getDocumentForAllStocks();
    Document getDocumentFromInputStream(InputStream inputStream);
}
