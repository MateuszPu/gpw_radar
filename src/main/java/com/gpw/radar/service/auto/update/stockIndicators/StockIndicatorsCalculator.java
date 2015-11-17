package com.gpw.radar.service.auto.update.stockIndicators;

import com.gpw.radar.domain.stock.StockIndicators;

import java.util.List;

public interface StockIndicatorsCalculator {
	List<StockIndicators> calculateCurrentStockIndicators();
}
