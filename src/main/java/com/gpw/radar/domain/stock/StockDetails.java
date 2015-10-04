package com.gpw.radar.domain.stock;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gpw.radar.domain.util.CustomLocalDateSerializer;
import com.gpw.radar.domain.util.ISO8601LocalDateDeserializer;


/**
 * A StockDetails.
 */
@Entity
@Table(name = "STOCK_DETAILS")
public class StockDetails implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @NotNull
    @Column(name = "open_price", precision=10, scale=2, nullable = false)
    private BigDecimal openPrice;

    @NotNull
    @Column(name = "max_price", precision=10, scale=2, nullable = false)
    private BigDecimal maxPrice;

    @NotNull
    @Column(name = "min_price", precision=10, scale=2, nullable = false)
    private BigDecimal minPrice;

    @NotNull
    @Column(name = "close_price", precision=10, scale=2, nullable = false)
    private BigDecimal closePrice;

    @NotNull
    @Column(name = "volume", nullable = false)
    private Long volume;
    
    @ManyToOne
    @JoinColumn(name="stock_id", foreignKey = @ForeignKey(name="FK_stock"))
    private Stock stock;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
