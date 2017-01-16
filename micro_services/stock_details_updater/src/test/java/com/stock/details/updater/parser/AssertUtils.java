package com.stock.details.updater.parser;

import com.stock.details.updater.model.StockDetails;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class AssertUtils {

    private StockDetails stockDetails;

    private AssertUtils(StockDetails stockDetails) {
        this.stockDetails = stockDetails;
    }

    public static AssertUtils assertStockDetails(StockDetails stockDetails) {
        return new AssertUtils(stockDetails);
    }

    public AssertUtils hasCorrectOpenPrice(BigDecimal openPrice) {
        assertThat(stockDetails.getOpenPrice()).isEqualTo(openPrice);
        return this;
    }

    public AssertUtils hasCorrectClosePrice(BigDecimal closePrice) {
        assertThat(stockDetails.getClosePrice()).isEqualTo(closePrice);
        return this;
    }

    public AssertUtils hasCorrectMaxPrice(BigDecimal maxPrice) {
        assertThat(stockDetails.getMaxPrice()).isEqualTo(maxPrice);
        return this;
    }

    public AssertUtils hasCorrectMinPrice(BigDecimal minPrice) {
        assertThat(stockDetails.getMinPrice()).isEqualTo(minPrice);
        return this;
    }

    public AssertUtils hasCorrectTransactionNumber(Long transactionNumber) {
        assertThat(stockDetails.getTransactionsNumber()).isEqualTo(transactionNumber);
        return this;
    }

    public AssertUtils hasCorrectVolume(Long volume) {
        assertThat(stockDetails.getVolume()).isEqualTo(volume);
        return this;
    }

    public AssertUtils hasCorrectDate(LocalDate date) {
        assertThat(stockDetails.getDate()).isEqualTo(date);
        return this;
    }

    public AssertUtils hasCorrectStockTicker(String ticker) {
        assertThat(stockDetails.getStock().getTicker()).isEqualTo(ticker);
        return this;
    }

    public AssertUtils hasCorrectStockShortName(String shortName) {
        assertThat(stockDetails.getStock().getShortName()).isEqualTo(shortName);
        return this;
    }

}
