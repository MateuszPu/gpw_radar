package com.stock.details.updater;

import com.stock.details.updater.model.StockDetails;

import java.io.IOException;
import java.util.List;

public interface StockDetailsParser {
    List<StockDetails> getCurrentStockDetails() throws IOException;
}
