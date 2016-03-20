package com.gpw.radar.domain.stock;

import com.gpw.radar.domain.enumeration.StockTicker;


public final class StockStatistic implements Comparable<Object>{

    private StockTicker gpwStockTicker;
    private double statisticValue;

    public StockStatistic(double statisticValue, StockTicker gpwStockTicker) {
        this.statisticValue = statisticValue;
        this.gpwStockTicker = gpwStockTicker;
    }

    public StockTicker getGpwStockTicker() {
        return gpwStockTicker;
    }

    public void setGpwStockTicker(StockTicker gpwStockTicker) {
        this.gpwStockTicker = gpwStockTicker;
    }

    public double getStatisticValue() {
        return statisticValue;
    }

    public void setStatisticValue(double statisticValue) {
        this.statisticValue = statisticValue;
    }

    @Override
    public int compareTo(Object obj) {
        StockStatistic otherStockStatistic = (StockStatistic) obj;
        return (this.statisticValue > otherStockStatistic.statisticValue) ? -1: (this.statisticValue < otherStockStatistic.statisticValue) ? 1:0 ;
    }
}
