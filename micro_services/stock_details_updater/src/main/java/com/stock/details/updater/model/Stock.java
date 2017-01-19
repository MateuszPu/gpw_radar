package com.stock.details.updater.model;

public class Stock {

    private String ticker;
    private String shortName;

    public Stock(String ticker, String shortName) {
        this.ticker = ticker;
        this.shortName = shortName;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
