package com.gpw.radar.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gpw.radar.domain.util.CustomLocalDateSerializer;
import com.gpw.radar.domain.util.ISO8601LocalDateDeserializer;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;


/**
 * A StockDetails.
 */
@Entity
@Table(name = "STOCKDETAILS")
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

    @Column(name = "average_volume10_days", precision=10, scale=2, nullable = false)
    private BigDecimal averageVolume10Days;

    @Column(name = "average_volume30_days", precision=10, scale=2, nullable = false)
    private BigDecimal averageVolume30Days;

    @Column(name = "volume_ratio10", precision=10, scale=2, nullable = false)
    private BigDecimal volumeRatio10;

    @Column(name = "volume_ratio30", precision=10, scale=2, nullable = false)
    private BigDecimal volumeRatio30;

    @Column(name = "percent_return", precision=10, scale=2, nullable = false)
    private BigDecimal percentReturn;

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

    public BigDecimal getAverageVolume10Days() {
        return averageVolume10Days;
    }

    public void setAverageVolume10Days(BigDecimal averageVolume10Days) {
        this.averageVolume10Days = averageVolume10Days;
    }

    public BigDecimal getAverageVolume30Days() {
        return averageVolume30Days;
    }

    public void setAverageVolume30Days(BigDecimal averageVolume30Days) {
        this.averageVolume30Days = averageVolume30Days;
    }

    public BigDecimal getVolumeRatio10() {
        return volumeRatio10;
    }

    public void setVolumeRatio10(BigDecimal volumeRatio10) {
        this.volumeRatio10 = volumeRatio10;
    }

    public BigDecimal getVolumeRatio30() {
        return volumeRatio30;
    }

    public void setVolumeRatio30(BigDecimal volumeRatio30) {
        this.volumeRatio30 = volumeRatio30;
    }

    public BigDecimal getPercentReturn() {
        return percentReturn;
    }

    public void setPercentReturn(BigDecimal percentReturn) {
        this.percentReturn = percentReturn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StockDetails stockDetails = (StockDetails) o;

        if ( ! Objects.equals(id, stockDetails.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "StockDetails{" +
                "id=" + id +
                ", date='" + date + "'" +
                ", openPrice='" + openPrice + "'" +
                ", maxPrice='" + maxPrice + "'" +
                ", minPrice='" + minPrice + "'" +
                ", closePrice='" + closePrice + "'" +
                ", volume='" + volume + "'" +
                ", averageVolume10Days='" + averageVolume10Days + "'" +
                ", averageVolume30Days='" + averageVolume30Days + "'" +
                ", volumeRatio10='" + volumeRatio10 + "'" +
                ", volumeRatio30='" + volumeRatio30 + "'" +
                ", percentReturn='" + percentReturn + "'" +
                '}';
    }
}
