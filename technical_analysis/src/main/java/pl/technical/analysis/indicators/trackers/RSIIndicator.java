package pl.technical.analysis.indicators.trackers;

import pl.technical.analysis.Tickable;
import pl.technical.analysis.indicators.Indicator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Relative strength index indicator.
 * <p>
 * More information regards to indicator: http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:moving_averages
 */
public class RSIIndicator extends Indicator {

    private BigDecimal averageGain = BigDecimal.ZERO;
    private BigDecimal averageLoss = BigDecimal.ZERO;
    private final int scale = 5;

    public RSIIndicator(List<Tickable> ticks, int period) {
        super(ticks, period);
    }

    @Override
    protected void calculateIndicator() {
        List<BigDecimal> closesPrices = super.getClosePrices();

        for (int i = 0; i < closesPrices.size() - super.period + 1; i++) {
            BigDecimal rsIndicator = calculateRsIndicator(closesPrices, i);

            if (rsIndicator.doubleValue() > 0) {
                BigDecimal rsiIndicator = super.hundred.subtract((super.hundred.divide((BigDecimal.ONE.add(rsIndicator)), 2, BigDecimal.ROUND_HALF_UP)));
                indicators.add(rsiIndicator.doubleValue());
            } else {
                indicators.add(0.0);
            }
        }
    }

    private BigDecimal calculateRsIndicator(List<BigDecimal> closesPrices, int i) {
        List<BigDecimal> gains = calculateGains(closesPrices);
        List<BigDecimal> periodGains = gains.subList(i, super.period + i);
        if (i == 0) {
            averageGain = calculateAverageGain(periodGains);
            averageLoss = calculateAverageLoss(periodGains);
        } else {
            int val = super.period - 1;
            BigDecimal multiplicand = BigDecimal.valueOf(val);
            BigDecimal currentGain = periodGains.get(val);

            if (currentGain.doubleValue() >= 0) {
                averageGain = (averageGain.multiply(multiplicand).add(gains.get(val + i))).divide(BigDecimal.valueOf(super.period), scale, BigDecimal.ROUND_HALF_UP);
                averageLoss = (averageLoss.multiply(multiplicand)).divide(BigDecimal.valueOf(super.period), scale, BigDecimal.ROUND_HALF_UP);
            } else {
                averageGain = (averageGain.multiply(multiplicand).divide(BigDecimal.valueOf(super.period), scale, BigDecimal.ROUND_HALF_UP));
                averageLoss = (averageLoss.multiply(multiplicand).subtract(gains.get(val + i))).divide(BigDecimal.valueOf(super.period), scale, BigDecimal.ROUND_HALF_UP);
            }
        }

        return averageGain.divide(averageLoss, scale, BigDecimal.ROUND_HALF_UP);
    }

    private List<BigDecimal> calculateGains(List<BigDecimal> closesPrices) {
        List<BigDecimal> gains = new ArrayList<>();
        gains.add(BigDecimal.ZERO);

        for (int i = 1; i < closesPrices.size(); i++) {
            gains.add(closesPrices.get(i).subtract(closesPrices.get(i - 1)));
        }

        return gains;
    }

    private BigDecimal calculateAverageLoss(List<BigDecimal> gains) {
        BigDecimal divider = BigDecimal.valueOf(super.period);
        BigDecimal averageLoss = gains
                .stream()
                .filter(e -> e.doubleValue() < 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(divider, 10, RoundingMode.HALF_UP);

        return averageLoss.multiply(BigDecimal.valueOf(-1));
    }

    private BigDecimal calculateAverageGain(List<BigDecimal> gains) {
        BigDecimal divider = BigDecimal.valueOf(super.period);
        BigDecimal averageGain = gains
                .stream()
                .filter(e -> e.doubleValue() >= 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(divider, 10, RoundingMode.HALF_UP);

        return averageGain;

    }
}
