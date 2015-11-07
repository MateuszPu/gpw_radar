package com.gpw.radar.service.auto.update.stockDetails;

import java.time.LocalDate;
import java.util.List;

import com.gpw.radar.domain.stock.StockDetails;

public interface StockDetailsParser {
	List<StockDetails> getCurrentStockDetails();
	void setQutesDate (LocalDate quotesDate);
}
