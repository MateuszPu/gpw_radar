package pl.technical.analysis;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface Tickable {
    LocalDate getDate();

    BigDecimal getOpenPrice();

    BigDecimal getMinPrice();

    BigDecimal getMaxPrice();

    BigDecimal getClosePrice();

    long getVolume();

    int getNumberOfTransaction();
}
