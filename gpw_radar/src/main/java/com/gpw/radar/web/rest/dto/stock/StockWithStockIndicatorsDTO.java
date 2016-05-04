package com.gpw.radar.web.rest.dto.stock;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class StockWithStockIndicatorsDTO {

    private BigDecimal percentReturn;
    @JsonProperty("stockId")
    private Long id;
    private String stockName;
    private String stockTicker;
    private String stockShortName;

    public BigDecimal getPercentReturn() {
        return percentReturn;
    }

    public void setPercentReturn(BigDecimal percentReturn) {
        this.percentReturn = percentReturn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getStockTicker() {
        return stockTicker;
    }

    public void setStockTicker(String stockTicker) {
        this.stockTicker = stockTicker;
    }

    public String getStockShortName() {
        return stockShortName;
    }

    public void setStockShortName(String stockShortName) {
        this.stockShortName = stockShortName;
    }
}
