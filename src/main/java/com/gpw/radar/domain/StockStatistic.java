package com.gpw.radar.domain;

import com.gpw.radar.domain.enumeration.Tickers;


public final class StockStatistic implements Comparable{

    private Tickers gpwStockTicker;
    private double statisticValue;

    public StockStatistic(double statisticValue, Tickers gpwStockTicker) {
        this.statisticValue = statisticValue;
        this.gpwStockTicker = gpwStockTicker;
    }

    public Tickers getGpwStockTicker() {
        return gpwStockTicker;
    }

    public void setGpwStockTicker(Tickers gpwStockTicker) {
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