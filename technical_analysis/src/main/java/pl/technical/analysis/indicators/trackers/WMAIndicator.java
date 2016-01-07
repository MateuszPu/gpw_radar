package pl.technical.analysis.indicators.trackers;

import pl.technical.analysis.Tickable;
import pl.technical.analysis.indicators.Indicator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple moving average indicator.
 * <p>
 * More information regards to indicator: http://www.barchart.com/education/std_studies.php?what=std_wma
 */
public class WMAIndicator extends Indicator {

    public WMAIndicator(List<Tickable> ticks, int period) {
        super(ticks, period);
    }

    @Override
    protected void calculateIndicator() {
        List<BigDecimal> closesPrices = super.getClosePrices();

        for (int i = 0; i < closesPrices.size() - super.period + 1; i++) {
            List<BigDecimal> closes = closesPrices.subList(i, super.period + i);
            List<BigDecimal> closesWeighted = new ArrayList<>();
            for (int j = 0; j < closes.size(); j++) {
                closesWeighted.add(closes.get(j).multiply(BigDecimal.valueOf(j+1)));
            }

            BigDecimal sum = closesWeighted
                    .stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal weight = calculateDivider(super.period);
            BigDecimal result = sum.divide(weight, 2, RoundingMode.HALF_UP);
            super.indicators.add(result.doubleValue());
        }
    }

    private BigDecimal calculateDivider(int period) {
        int result = (int) ((1 + period) / 2.0 * period);
        return BigDecimal.valueOf(result);
    }
}
