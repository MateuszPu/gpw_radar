package com.gpw.radar.service.correlation;

import java.util.Comparator;

public final class CorrelationResult implements Comparable<CorrelationResult>{
    private final String ticker;
    private final double correlation;

    public CorrelationResult(String ticker, double correlation) {
        this.ticker = ticker;
        this.correlation = correlation;
    }

    public String getTicker() {
        return ticker;
    }

    public double getCorrelation() {
        return correlation;
    }


    @Override
    public int compareTo(CorrelationResult o) {
        return Comparator.comparing(CorrelationResult::getCorrelation)
            .thenComparing(CorrelationResult::getTicker)
            .compare(this, o);
    }
}
