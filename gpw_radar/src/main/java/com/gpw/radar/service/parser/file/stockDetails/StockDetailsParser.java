package com.gpw.radar.service.parser.file.stockDetails;

import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockDetails;

import java.io.InputStream;
import java.util.List;

public interface StockDetailsParser {

    public List<StockDetails> parseStockDetails(Stock stock, InputStream st);
}
