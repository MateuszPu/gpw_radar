package com.gpw.radar.web.rest.dto.stock;

import java.math.BigDecimal;

public class StockWithStockIndicatorsDTO {

    private BigDecimal percentReturn;
    private Long stockId;
    private String stockName;
    private String stockTicker;
    private String stockShortName;

    public BigDecimal getPercentReturn() {
        return percentReturn;
    }

    public void setPercentReturn(BigDecimal percentReturn) {
        this.percentReturn = percentReturn;
    }

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
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
