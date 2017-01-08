package com.stock.details.updater.model;

public class Stock {

    String ticker;
    String stockShortName;

    public Stock(String ticker, String stockShortName) {
        this.ticker = ticker;
        this.stockShortName = stockShortName;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getStockShortName() {
        return stockShortName;
    }

    public void setStockShortName(String stockShortName) {
        this.stockShortName = stockShortName;
    }
}
