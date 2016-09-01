package com.gpw.radar.service.auto.update.stockDetails.indicators;

import com.gpw.radar.domain.stock.StockIndicators;

import java.util.List;

public interface StockIndicatorsCalculator {
    List<StockIndicators> calculateCurrentStockIndicators();
}
