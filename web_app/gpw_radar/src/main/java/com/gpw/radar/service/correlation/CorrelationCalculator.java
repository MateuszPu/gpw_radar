package com.gpw.radar.service.correlation;

@FunctionalInterface
interface CorrelationCalculator<SOURCE, TARGET, RESULT> {
    RESULT apply(SOURCE source, TARGET target);
}
