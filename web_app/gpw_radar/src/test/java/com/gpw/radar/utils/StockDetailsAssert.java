package com.gpw.radar.utils;

import com.gpw.radar.elasticsearch.domain.stockdetails.StockDetails;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class StockDetailsAssert {

    private StockDetails stockDetails;

    private StockDetailsAssert(StockDetails stockDetails) {
        this.stockDetails = stockDetails;
    }

    public static StockDetailsAssert create(StockDetails stockDetails) {
        return new StockDetailsAssert(stockDetails);
    }

    public StockDetailsAssert hasCorrectOpenPrice(BigDecimal openPrice) {
        assertThat(stockDetails.getOpenPrice()).isEqualTo(openPrice);
        return this;
    }

    public StockDetailsAssert hasCorrectClosePrice(BigDecimal closePrice) {
        assertThat(stockDetails.getClosePrice()).isEqualTo(closePrice);
        return this;
    }

    public StockDetailsAssert hasCorrectMaxPrice(BigDecimal maxPrice) {
        assertThat(stockDetails.getMaxPrice()).isEqualTo(maxPrice);
        return this;
    }

    public StockDetailsAssert hasCorrectMinPrice(BigDecimal minPrice) {
        assertThat(stockDetails.getMinPrice()).isEqualTo(minPrice);
        return this;
    }

    public StockDetailsAssert hasCorrectTransactionNumber(Long transactionNumber) {
        assertThat(stockDetails.getTransactionsNumber()).isEqualTo(transactionNumber);
        return this;
    }

    public StockDetailsAssert hasCorrectVolume(Long volume) {
        assertThat(stockDetails.getVolume()).isEqualTo(volume);
        return this;
    }

    public StockDetailsAssert hasCorrectDate(LocalDate date) {
        assertThat(stockDetails.getDate()).isEqualTo(date);
        return this;
    }

    public StockDetailsAssert hasCorrectStockTicker(String ticker) {
        assertThat(stockDetails.getStock().getTicker()).isEqualTo(ticker);
        return this;
    }

    public StockDetailsAssert hasCorrectStockShortName(String shortName) {
        assertThat(stockDetails.getStock().getShortName()).isEqualTo(shortName);
        return this;
    }
}
