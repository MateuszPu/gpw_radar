package com.gpw.radar.domain.stock;

public final class StockStatistic implements Comparable<Object> {

    private String gpwStockTicker;
    private double statisticValue;

    public StockStatistic(double statisticValue, String gpwStockTicker) {
        this.statisticValue = statisticValue;
        this.gpwStockTicker = gpwStockTicker;
    }

    public String getGpwStockTicker() {
        return gpwStockTicker;
    }

    public void setGpwStockTicker(String gpwStockTicker) {
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
        return (this.statisticValue > otherStockStatistic.statisticValue) ? -1 : (this.statisticValue < otherStockStatistic.statisticValue) ? 1 : 0;
    }
}
