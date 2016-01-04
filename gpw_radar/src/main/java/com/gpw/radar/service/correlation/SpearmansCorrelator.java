package com.gpw.radar.service.correlation;

import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

public class SpearmansCorrelator implements Correlator {

	private SpearmansCorrelation spearmansCorrelation;

	public SpearmansCorrelator() {
		spearmansCorrelation = new SpearmansCorrelation();
	}

	@Override
	public double correlate(double[] sourceClosePrices, double[] targetClosePrices) {
		if (sourceClosePrices.length != targetClosePrices.length) {
            return 0.0;
        }
		return spearmansCorrelation.correlation(sourceClosePrices, targetClosePrices);
	}
}