package com.gpw.radar.domain.stock;

import com.gpw.radar.domain.util.LocalTimeConverter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * A Stock five minutes details.
 */
@Entity
@Table(name = "STOCK_FIVE_MINUTES_DETAILS")
public class StockFiveMinutesDetails {

    @Id
    @GenericGenerator(name = "seq_id", strategy = "com.gpw.radar.domain.generator.StringIdGenerator")
    @GeneratedValue(generator = "seq_id")
    private String id;

    @NotNull
    @Column(name = "date_event", nullable = false)
    private LocalDate date;

    @NotNull
    @Column(name = "time_event", nullable = false, columnDefinition = "time")
    @Convert(converter = LocalTimeConverter.class)
    private LocalTime time;

    @NotNull
    @Column(name = "volume", nullable = false)
    private long volume;

    @NotNull
    @Column(name = "cumulated_volume", nullable = false)
    private long cumulatedVolume;

    @Column(name = "ratio_volme")
    private double ratioVolume;

    @Transient
    private String stockTicker;

    @ManyToOne(fetch = FetchType.LAZY)
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

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public long getCumulatedVolume() {
        return cumulatedVolume;
    }

    public void setCumulatedVolume(long cumulatedVolume) {
        this.cumulatedVolume = cumulatedVolume;
    }

    public double getRatioVolume() {
        return ratioVolume;
    }

    public void setRatioVolume(double ratioVolume) {
        this.ratioVolume = ratioVolume;
    }

    public String getStockTicker() {
        return stockTicker;
    }

    public void setStockTicker(String stockTicker) {
        this.stockTicker = stockTicker;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }
}
