package com.gpw.radar.service.auto.update.stockDetails;

import java.util.List;

import org.joda.time.LocalDate;

import com.gpw.radar.domain.StockDetails;

public interface StockDetailsParser {
	List<StockDetails> getCurrentStockDetails();
	void setQutesDate (LocalDate quotesDate);
}
