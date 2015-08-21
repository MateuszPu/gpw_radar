package com.gpw.radar.service.correlation;

import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

import com.gpw.radar.domain.StockStatistic;
import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.repository.StockDetailsRepository;

public class SpearmansCorrelator extends Correlator {

	private SpearmansCorrelation spearmansCorrelation;

	public SpearmansCorrelator(StockTicker correlationForTicker, int period, StockDetailsRepository stockDetailsRepository) {
		super(correlationForTicker, period, stockDetailsRepository);
		spearmansCorrelation = new SpearmansCorrelation();
	}

	public void compute(StockTicker ticker) {
		double[] closePricesToCompare = getClosePrices(getContent(ticker));
		double correlation = 0.0;
		if (closePricesToCompare.length == sourceClosePrices.length) {
			correlation = spearmansCorrelation.correlation(sourceClosePrices, closePricesToCompare);
		}
		StockStatistic stockCorrelation = new StockStatistic(correlation, ticker);
		correlationTreeSet.add(stockCorrelation);
	}
}