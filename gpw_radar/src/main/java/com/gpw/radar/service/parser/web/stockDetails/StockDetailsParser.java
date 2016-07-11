package com.gpw.radar.service.parser.web.stockDetails;

import com.gpw.radar.domain.stock.StockDetails;

import java.time.LocalDate;
import java.util.List;

public interface StockDetailsParser {

    List<StockDetails> parseStockDetails(LocalDate date);
}
