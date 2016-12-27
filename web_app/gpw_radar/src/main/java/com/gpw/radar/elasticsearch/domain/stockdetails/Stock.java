package com.gpw.radar.elasticsearch.domain.stockdetails;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

public class Stock {

    @Field(type= FieldType.String, index = FieldIndex.not_analyzed)
    private String ticker;

    @Field(type= FieldType.String, index = FieldIndex.not_analyzed)
    private String stockName;

    @Field(type= FieldType.String, index = FieldIndex.not_analyzed)
    private String stockShortName;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getStockShortName() {
        return stockShortName;
    }

    public void setStockShortName(String stockShortName) {
        this.stockShortName = stockShortName;
    }
}
