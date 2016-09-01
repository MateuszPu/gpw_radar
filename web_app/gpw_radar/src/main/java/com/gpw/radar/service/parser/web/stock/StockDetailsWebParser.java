package com.gpw.radar.service.parser.web.stock;

import org.jsoup.nodes.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface StockDetailsWebParser extends StockDataNameParser {

    BigDecimal parseOpenPrice(Document doc);

    BigDecimal parseClosePrice(Document doc);

    BigDecimal parseMaxPrice(Document doc);

    BigDecimal parseMinPrice(Document doc);

    LocalDate parseDate(Document doc);

    Long parseVolume(Document doc);
}
