package com.gpw.radar.elasticsearch.domain.stockdetails;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

public class Stock {

    @Field(type= FieldType.String, index = FieldIndex.not_analyzed)
    private String ticker;

    @Field(type= FieldType.String, index = FieldIndex.not_analyzed)
    private String name;

    @Field(type= FieldType.String, index = FieldIndex.not_analyzed)
    private String shortName;

    public Stock() {
    }

    public Stock(String ticker, String name, String shortName) {
        this.ticker = ticker;
        this.name = name;
        this.shortName = shortName;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
