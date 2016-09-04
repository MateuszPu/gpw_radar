package com.gpw.radar.service.builders;

import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockDetails;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by mateusz on 28.05.2016.
 */
public class StockDetailsBuilder {

    private LocalDate date;
    private BigDecimal openPrice;
    private BigDecimal maxPrice;
    private BigDecimal minPrice;
    private BigDecimal closePrice;
    private Long volume;
    private Stock stock;

    public static StockDetailsBuilder stockDetailsBuilder() {
        return new StockDetailsBuilder();
    }

    public StockDetailsBuilder date(LocalDate date) {
        this.date = date;
        return this;
    }

    public StockDetailsBuilder openPrice(BigDecimal openPrice) {
        this.openPrice = openPrice;
        return this;
    }

    public StockDetailsBuilder openPrice(String openPrice) {
        this.openPrice = new BigDecimal(openPrice);
        return this;
    }

    public StockDetailsBuilder maxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
        return this;
    }

    public StockDetailsBuilder maxPrice(String maxPrice) {
        this.maxPrice = new BigDecimal(maxPrice);
        return this;
    }

    public StockDetailsBuilder minPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
        return this;
    }

    public StockDetailsBuilder minPrice(String minPrice) {
        this.minPrice = new BigDecimal(minPrice);
        return this;
    }

    public StockDetailsBuilder closePrice(BigDecimal closePrice) {
        this.closePrice = closePrice;
        return this;
    }

    public StockDetailsBuilder closePrice(String closePrice) {
        this.closePrice = new BigDecimal(closePrice);
        return this;
    }

    public StockDetailsBuilder volume(Long volume) {
        this.volume = volume;
        return this;
    }

    public StockDetailsBuilder stock(Stock stock) {
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
