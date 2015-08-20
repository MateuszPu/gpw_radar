package com.gpw.radar.service.correlation;

import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

import com.gpw.radar.domain.StockStatistic;
import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.repository.StockDetailsRepository;

public class SpearmansCorrelationImpl extends CorrelationVariables implements Correlation {

	private SpearmansCorrelation spearmansCorrelation;

	public SpearmansCorrelationImpl(StockTicker correlationForTicker, int period, StockDetailsRepository stockDetailsRepository) {
		super(correlationForTicker, period, stockDetailsRepository);
		spearmansCorrelation = new SpearmansCorrelation();
	}

	public void compute(StockTicker ticker) {
		double[] closePricesToCompare = getClosePrices(getContent(ticker));
		Double correlation = 0.0;
		if (closePricesToCompare.length == sourceClosePrices.length) {
			correlation = spearmansCorrelation.correlation(sourceClosePrices, closePricesToCompare);
		}
		StockStatistic stockCorrelation = new StockStatistic(correlation, ticker);
		correlationTreeSet.add(stockCorrelation);
	}
}