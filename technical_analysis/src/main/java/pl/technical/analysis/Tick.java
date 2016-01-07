package pl.technical.analysis;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class Tick implements Tickable {

    private final LocalDate date;
    private final BigDecimal openPrice;
    private final BigDecimal minPrice;
    private final BigDecimal maxPrice;
    private final BigDecimal closePrice;
    private final long volume;
    private final int numberOfTransaction;

    public Tick(LocalDate date, BigDecimal openPrice, BigDecimal minPrice, BigDecimal maxPrice, BigDecimal closePrice, long volume, int numberOfTransaction) {
        this.date = date;
        this.openPrice = openPrice;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.closePrice = closePrice;
        this.volume = volume;
        this.numberOfTransaction = numberOfTransaction;
    }

    public Tick(LocalDate date, double openPrice, double minPrice, double maxPrice, double closePrice, long volume, int numberOfTransaction) {
        this(date, BigDecimal.valueOf(openPrice), BigDecimal.valueOf(minPrice), BigDecimal.valueOf(maxPrice), BigDecimal.valueOf(closePrice), volume, numberOfTransaction);
    }

    public Tick(LocalDate date, float openPrice, float minPrice, float maxPrice, float closePrice, long volume, int numberOfTransaction) {
        this(date, BigDecimal.valueOf(openPrice), BigDecimal.valueOf(minPrice), BigDecimal.valueOf(maxPrice), BigDecimal.valueOf(closePrice), volume, numberOfTransaction);
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getOpenPrice() {
        return openPrice;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public BigDecimal getClosePrice() {
        return closePrice;
    }

    public Long getVolume() {
        return volume;
    }

    public Integer getNumberOfTransaction() {
        return numberOfTransaction;
    }
}
