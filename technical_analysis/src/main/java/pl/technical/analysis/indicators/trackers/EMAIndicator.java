package pl.technical.analysis.indicators.trackers;

import pl.technical.analysis.Tickable;
import pl.technical.analysis.indicators.Indicator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


/**
 * Exponential moving average indicator.
 * <p>
 * More information regards to indicator: http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:moving_averages
 */
public class EMAIndicator extends Indicator {

    public EMAIndicator(List<Tickable> ticks, int period) {
        super(ticks, period);
    }

    public void calculateIndicator() {
        List<BigDecimal> closesPrices = getClosePrices();
        BigDecimal divider = BigDecimal.valueOf(super.period);
        BigDecimal multiplier = (BigDecimal.valueOf(2).divide((BigDecimal.valueOf(super.period).add(BigDecimal.ONE)), 5, RoundingMode.HALF_UP));

        BigDecimal SMA = (closesPrices
                .subList(0, super.period)
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add))
                .divide(divider, 2, RoundingMode.HALF_UP);

        for (int i = super.period - 1; i < closesPrices.size(); i++) {
            BigDecimal currentClosePrice = closesPrices.get(i);
            BigDecimal EMA = BigDecimal.ZERO;
            if (i == super.period - 1) {
                EMA = SMA;
            } else {
                EMA = multiplier.multiply((currentClosePrice.subtract(BigDecimal.valueOf(getLast())))).add(BigDecimal.valueOf(getLast()));
            }
            super.indicators.add(EMA.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        }
    }
}
