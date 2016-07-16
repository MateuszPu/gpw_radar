package com.gpw.radar.domain.stock;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gpw.radar.service.util.RandomUtil;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;


/**
 * A StockDetails.
 */
@Entity
@Table(name = "STOCK_DETAILS")
public class StockDetails implements Serializable {

    @Id
    private String id = RandomUtil.generateId();

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @NotNull
    @Column(name = "open_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal openPrice;

    @NotNull
    @Column(name = "max_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal maxPrice;

    @NotNull
    @Column(name = "min_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal minPrice;

    @NotNull
    @Column(name = "close_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal closePrice;

    @NotNull
    @Column(name = "transactions_number", nullable = false)
    private Long transactionsNumber = 0L;

    @NotNull
    @Column(name = "volume", nullable = false)
    private Long volume;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", foreignKey = @ForeignKey(name = "FK_stock"))
    private Stock stock;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(BigDecimal openPrice) {
        this.openPrice = openPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(BigDecimal closePrice) {
        this.closePrice = closePrice;
    }

    public Long getTransactionsNumber() {
        return transactionsNumber;
    }

    public void setTransactionsNumber(Long transactionsNumber) {
        this.transactionsNumber = transactionsNumber;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }
}
