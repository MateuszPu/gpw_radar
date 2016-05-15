package com.gpw.radar.service.correlation;

import org.apache.commons.math3.stat.correlation.KendallsCorrelation;

public class KendallsCorrelator implements Correlator{

	private KendallsCorrelation kendallsCorrelation;

	public KendallsCorrelator() {
		kendallsCorrelation = new KendallsCorrelation();
	}

	@Override
	public double correlate(double[] sourceClosePrices, double[] targetClosePrices) {
		return kendallsCorrelation.correlation(sourceClosePrices, targetClosePrices);
	}
}
