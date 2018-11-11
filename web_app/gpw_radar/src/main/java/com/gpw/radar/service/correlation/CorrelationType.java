package com.gpw.radar.service.correlation;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.correlation.KendallsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

public enum CorrelationType {

    KENDALLS((source, compareTo) -> new KendallsCorrelation().correlation(ArrayUtils
        .toPrimitive(source), ArrayUtils.toPrimitive(compareTo))),
    PEARSONS((source, compareTo) -> new SpearmansCorrelation().correlation(ArrayUtils
        .toPrimitive(source), ArrayUtils.toPrimitive(compareTo))),
    SPEARMANS((source, compareTo) -> new SpearmansCorrelation().correlation(ArrayUtils
        .toPrimitive(source), ArrayUtils.toPrimitive(compareTo)));

    CorrelationType(CorrelationCalculator<Double[], Double[], Double> correlationCalculator) {
        this.correlationCalculator = correlationCalculator;
    }

    private CorrelationCalculator<Double[], Double[], Double> correlationCalculator;

    public double calculate(double[] source, double[] compareTo) {
        return this.correlationCalculator.apply(ArrayUtils.toObject(source), ArrayUtils.toObject(compareTo));
    }
}
