package com.gpw.radar.domain.stock;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * A Stock five minutes details.
 */
@Entity
@Table(name = "STOCK_FIVE_MINUTES_DETAILS")
public class StockFiveMinutesDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "date_event", nullable = false)
    private LocalDateTime date;

    @NotNull
    @Column(name = "time_event", nullable = false)
    private LocalTime time;

    @NotNull
    @Column(name = "volume", nullable = false)
    private Long volume;

    @NotNull
    @Column(name = "cumulated_volume", nullable = false)
    private Long cumulatedVolume;

    @ManyToOne
    @JoinColumn(name="stock_id", foreignKey = @ForeignKey(name="FK_stock"))
    private Stock stock;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }

    public Long getCumulatedVolume() {
        return cumulatedVolume;
    }

    public void setCumulatedVolume(Long cumulatedVolume) {
        this.cumulatedVolume = cumulatedVolume;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }
}
