package com.gpw.radar.service.correlation;


public interface Correlator {
    double correlate(final double[] sourceClosePrices, final double[] targetClosePrices);
}
