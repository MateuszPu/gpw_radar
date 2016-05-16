package com.gpw.radar.service.correlation;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

public class PearsonCorrelator implements Correlator {

	private PearsonsCorrelation pearsonsCorrelation;

	public PearsonCorrelator() {
		pearsonsCorrelation = new PearsonsCorrelation();
	}

	@Override
	public double correlate(double[] sourceClosePrices, double[] targetClosePrices) {
		return pearsonsCorrelation.correlation(sourceClosePrices, targetClosePrices);
	}
}
