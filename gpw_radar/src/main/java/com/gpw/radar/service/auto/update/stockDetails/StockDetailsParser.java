package com.gpw.radar.service.auto.update.stockDetails;

import com.gpw.radar.domain.stock.StockDetails;

import java.time.LocalDate;
import java.util.List;

public interface StockDetailsParser {
	List<StockDetails> getCurrentStockDetails();
	void setQuotesDate(LocalDate quotesDate);
}
