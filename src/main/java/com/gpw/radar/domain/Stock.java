package com.gpw.radar.domain;


import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

import com.gpw.radar.domain.enumeration.GpwStockTicker;

/**
 * A Stock.
 */
@Entity
@Table(name = "STOCK")
public class Stock implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "y", nullable = false)
    private GpwStockTicker y;

    @Column(name = "stock_name")
    private String stockName;

    @Column(name = "stock_short_name")
    private String stockShortName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GpwStockTicker getY() {
        return y;
    }

    public void setY(GpwStockTicker y) {
        this.y = y;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Stock stock = (Stock) o;

        if ( ! Objects.equals(id, stock.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Stock{" +
                "id=" + id +
                ", y='" + y + "'" +
                ", stockName='" + stockName + "'" +
                ", stockShortName='" + stockShortName + "'" +
                '}';
    }
}
