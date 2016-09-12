package com.stock.details.updater;

import com.stock.details.updater.model.StockDetails;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface StockDetailsParser {
    List<StockDetails> getCurrentStockDetails(Elements tableRows, LocalDate date) throws IOException;
}
