package com.gpw.radar.service.parser.web.stockDetails;

import com.gpw.radar.elasticsearch.stockdetails.StockDetails;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface StockDetailsParser {

    List<StockDetails> parseStockDetails(LocalDate date) throws IOException;
}
