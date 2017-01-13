package com.gpw.radar.service.technical.indicators;

import com.gpw.radar.elasticsearch.domain.stockdetails.StockDetails;
import pl.technical.analysis.Tickable;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TickAdapter implements Tickable {

    private StockDetails stockDetails;

    public TickAdapter(StockDetails stockDetails) {
        this.stockDetails = stockDetails;
    }

    @Override
    public LocalDate getDate() {
        return stockDetails.getDate();
    }

    @Override
    public BigDecimal getOpenPrice() {
        return stockDetails.getOpenPrice();
    }

    @Override
    public BigDecimal getMinPrice() {
        return stockDetails.getMinPrice();
    }

    @Override
    public BigDecimal getMaxPrice() {
        return stockDetails.getMaxPrice();
    }

    @Override
    public BigDecimal getClosePrice() {
        return stockDetails.getClosePrice();
    }

    @Override
    public Long getVolume() {
        return stockDetails.getVolume();
    }

    @Override
    public Integer getNumberOfTransaction() {
        throw new IllegalArgumentException("not implemented yet");
    }
}
