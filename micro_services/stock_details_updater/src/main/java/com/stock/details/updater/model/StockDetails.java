package com.stock.details.updater.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class StockDetails {

    private Stock stock;
    private LocalDate date;
    private BigDecimal openPrice;
    private BigDecimal maxPrice;
    private BigDecimal minPrice;
    private BigDecimal closePrice;
    private Long volume = 0L;
    private Long transactionsNumber = 0L;

    public StockDetails() {
    }

    public StockDetails(String lastPrice, Stock stock) {
        setOpenPrice(new BigDecimal(lastPrice));
        setMaxPrice(new BigDecimal(lastPrice));
        setMinPrice(new BigDecimal(lastPrice));
        setClosePrice(new BigDecimal(lastPrice));
        this.stock = stock;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(BigDecimal openPrice) {
        this.openPrice = openPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(BigDecimal closePrice) {
        this.closePrice = closePrice;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }

    public Long getTransactionsNumber() {
        return transactionsNumber;
    }

    public void setTransactionsNumber(Long transactionsNumber) {
        this.transactionsNumber = transactionsNumber;
    }
}
