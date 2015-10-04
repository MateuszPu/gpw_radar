package com.gpw.radar.domain.stock;

import com.gpw.radar.domain.enumeration.Ticker;


public final class StockStatistic implements Comparable<Object>{

    private Ticker gpwStockTicker;
    private double statisticValue;

    public StockStatistic(double statisticValue, Ticker gpwStockTicker) {
        this.statisticValue = statisticValue;
        this.gpwStockTicker = gpwStockTicker;
    }

    public Ticker getGpwStockTicker() {
        return gpwStockTicker;
    }

    public void setGpwStockTicker(Ticker gpwStockTicker) {
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