package pl.technical.analysis.indicators.trackers;

import pl.technical.analysis.Tickable;
import pl.technical.analysis.indicators.Indicator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Simple moving average indicator.
 * <p>
 * More information regards to indicator: http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:moving_averages
 */
public final class SMAIndicator extends Indicator {

    public SMAIndicator(List<Tickable> ticks, int period) {
        super(ticks, period);
    }

    public void calculateIndicator() {
        List<BigDecimal> closesPrices = super.getClosePrices();
        BigDecimal divider = BigDecimal.valueOf(super.period);

        for (int i = 0; i < closesPrices.size() - super.period + 1; i++) {
            BigDecimal average = (closesPrices
                    .subList(i, super.period + i)
                    .stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add))
                    .divide(divider, 2, RoundingMode.HALF_UP);
            super.indicators.add(average.doubleValue());
        }
    }
}