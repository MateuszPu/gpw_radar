package com.gpw.radar.service.correlation;

public interface CorrelationStrategy {

    double correlate(final double[] sourceClosePrices, final double[] targetClosePrices);
}