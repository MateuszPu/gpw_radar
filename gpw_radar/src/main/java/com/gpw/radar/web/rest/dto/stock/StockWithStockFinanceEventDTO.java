package com.gpw.radar.web.rest.dto.stock;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class StockWithStockFinanceEventDTO {

    @JsonProperty("start")
    private LocalDate date;
    @JsonIgnore
    private String stockTicker;
    @JsonIgnore
    private String stockStockName;
    @JsonProperty("message")
    private String message;

    @JsonProperty("title")
    public String getTitle() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(stockTicker.toUpperCase());
        sb.append("] ");
        sb.append(stockStockName);
        return sb.toString();
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getStockTicker() {
        return stockTicker;
    }

    public void setStockTicker(String stockTicker) {
        this.stockTicker = stockTicker;
    }

    public String getStockStockName() {
        return stockStockName;
    }

    public void setStockStockName(String stockStockName) {
        this.stockStockName = stockStockName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
