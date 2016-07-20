package com.gpw.radar.domain.stock;

import com.gpw.radar.domain.util.LocalTimeConverter;
import com.gpw.radar.service.util.RandomUtil;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

/**
 * A Stock five minutes indicators.
 */
@Entity
@Table(name = "STOCK_FIVE_MINUTES_INDICATORS")
public class StockFiveMinutesIndicators {

    @Id
    private String id = RandomUtil.generateId();

    @NotNull
    @Column(name = "time_event", nullable = false, columnDefinition = "time")
    @Convert(converter = LocalTimeConverter.class)
    private LocalTime time;

    @NotNull
    @Column(name = "average_volume", nullable = false)
    private double averageVolume;

    @ManyToOne
    @JoinColumn(name="stock_id", foreignKey = @ForeignKey(name="FK_stock"))
    private Stock stock;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public double getAverageVolume() {
        return averageVolume;
    }

    public void setAverageVolume(double averageVolume) {
        this.averageVolume = averageVolume;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

}
