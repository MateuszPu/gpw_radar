package com.gpw.radar.service.correlation;

import java.util.List;
import java.util.TreeSet;

import com.gpw.radar.domain.StockStatistic;
import com.gpw.radar.domain.enumeration.StockTicker;

public interface Correlation {
	void compute(StockTicker ticker);
	List<StockTicker> getTickersWithOutOneAnalysed();
	TreeSet<StockStatistic> getCorrelationTreeSet();
}
