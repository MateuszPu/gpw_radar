package com.gpw.radar.web.rest.dto.stock;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class StockWithStockIndicatorsDTO {

    @JsonProperty("percentReturn")
    private BigDecimal stockIndicatorsPercentReturn;
    @JsonProperty("stockId")
    private String id;
    private String stockName;
    private String stockTicker;
    private String stockShortName;

    public StockWithStockIndicatorsDTO() {
    }

    public StockWithStockIndicatorsDTO(String ticker) {
        this.setStockTicker(ticker);
    }

    public BigDecimal getStockIndicatorsPercentReturn() {
        return stockIndicatorsPercentReturn;
    }

    public void setStockIndicatorsPercentReturn(BigDecimal stockIndicatorsPercentReturn) {
        this.stockIndicatorsPercentReturn = stockIndicatorsPercentReturn;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
