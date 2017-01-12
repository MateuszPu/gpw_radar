package com.gpw.radar.service.builders;


import com.gpw.radar.elasticsearch.domain.stockdetails.Stock;
import com.gpw.radar.elasticsearch.domain.stockdetails.StockDetails;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by mateusz on 28.05.2016.
 */
public class StockDetailsEsBuilder {

    private LocalDate date;
    private BigDecimal openPrice;
    private BigDecimal maxPrice;
    private BigDecimal minPrice;
    private BigDecimal closePrice;
    private Long volume;
    private Stock stock;

    public static StockDetailsEsBuilder stockDetailsBuilder() {
        return new StockDetailsEsBuilder();
    }

    public StockDetailsEsBuilder date(LocalDate date) {
        this.date = date;
        return this;
    }

    public StockDetailsEsBuilder openPrice(BigDecimal openPrice) {
        this.openPrice = openPrice;
        return this;
    }

    public StockDetailsEsBuilder openPrice(String openPrice) {
        this.openPrice = new BigDecimal(openPrice);
        return this;
    }

    public StockDetailsEsBuilder maxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
        return this;
    }

    public StockDetailsEsBuilder maxPrice(String maxPrice) {
        this.maxPrice = new BigDecimal(maxPrice);
        return this;
    }

    public StockDetailsEsBuilder minPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
        return this;
    }

    public StockDetailsEsBuilder minPrice(String minPrice) {
        this.minPrice = new BigDecimal(minPrice);
        return this;
    }

    public StockDetailsEsBuilder closePrice(BigDecimal closePrice) {
        this.closePrice = closePrice;
        return this;
    }

    public StockDetailsEsBuilder closePrice(String closePrice) {
        this.closePrice = new BigDecimal(closePrice);
        return this;
    }

    public StockDetailsEsBuilder volume(Long volume) {
        this.volume = volume;
        return this;
    }

    public StockDetailsEsBuilder stock(Stock stock) {
        this.stock = stock;
        return this;
    }

    public StockDetails build() {
        StockDetails stockDetails = new StockDetails();
        stockDetails.setDate(date);
        stockDetails.setOpenPrice(openPrice);
        stockDetails.setMaxPrice(maxPrice);
        stockDetails.setMinPrice(minPrice);
        stockDetails.setClosePrice(closePrice);
        stockDetails.setVolume(volume);
        stockDetails.setStock(stock);
        return stockDetails;
    }
}
