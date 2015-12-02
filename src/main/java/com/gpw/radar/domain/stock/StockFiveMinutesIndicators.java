package com.gpw.radar.domain.stock;

import com.gpw.radar.domain.util.LocalTimeConverter;

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
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "idGenerator")
    @TableGenerator(table = "hibernate_sequences_table", name = "idGenerator", pkColumnName = "pk",
        valueColumnName = "value", pkColumnValue = "stock_five_minutes_indicators")
    private Long id;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
