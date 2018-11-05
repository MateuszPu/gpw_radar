package com.gpw.radar.domain.stock;

import java.util.Comparator;
import java.util.Objects;

public final class StockStatistic implements Comparable<Object> {

    private final String gpwStockTicker;
    private final double statisticValue;

    public StockStatistic(double statisticValue, String gpwStockTicker) {
        this.statisticValue = statisticValue;
        this.gpwStockTicker = gpwStockTicker;
    }

    String getGpwStockTicker() {
        return gpwStockTicker;
    }


    double getStatisticValue() {
        return statisticValue;
    }

    @Override
    public int compareTo(Object obj) {
        StockStatistic otherStockStatistic = (StockStatistic) obj;
        return Comparator.comparing(StockStatistic::getStatisticValue)
            .thenComparing(StockStatistic::getGpwStockTicker)
            .compare(this, otherStockStatistic);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockStatistic that = (StockStatistic) o;
        return Objects.equals(getGpwStockTicker(), that.getGpwStockTicker());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGpwStockTicker());
    }
}
