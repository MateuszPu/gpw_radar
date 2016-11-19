package com.stock.details.updater.parser.gpw;

import com.stock.details.updater.model.StockDetails;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface WebStockDetailsParser {
    List<StockDetails> getCurrentStockDetails(Elements tableRows, LocalDate date) throws IOException;
}
