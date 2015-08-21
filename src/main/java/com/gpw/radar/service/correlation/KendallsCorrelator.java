package com.gpw.radar.service.correlation;

import org.apache.commons.math3.stat.correlation.KendallsCorrelation;

import com.gpw.radar.domain.StockStatistic;
import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.repository.StockDetailsRepository;

public class KendallsCorrelator extends Correlator{
	
	private KendallsCorrelation kendallsCorrelation;
	
	public KendallsCorrelator(StockTicker correlationForTicker, int period, StockDetailsRepository stockDetailsRepository) {
		super(correlationForTicker, period, stockDetailsRepository);
		kendallsCorrelation = new KendallsCorrelation();
	}
	
	public void compute(StockTicker ticker) {
		double[] closePricesToCompare = getClosePrices(getContent(ticker));
		double correlation = 0.0;
		if (closePricesToCompare.length == sourceClosePrices.length) {
			correlation = kendallsCorrelation.correlation(sourceClosePrices, closePricesToCompare);
		}
		StockStatistic stockCorrelation = new StockStatistic(correlation, ticker);
		correlationTreeSet.add(stockCorrelation);
	}
}
