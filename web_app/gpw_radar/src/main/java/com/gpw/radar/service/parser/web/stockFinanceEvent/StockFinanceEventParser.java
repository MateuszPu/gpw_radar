package com.gpw.radar.service.parser.web.stockFinanceEvent;

import com.gpw.radar.domain.stock.StockFinanceEvent;
import org.jsoup.nodes.Document;

import java.util.List;

public interface StockFinanceEventParser {

    List<StockFinanceEvent> getStockFinanceEventFromWeb();

    List<StockFinanceEvent> getStockFinanceEvents(List<Document> documents);
}
