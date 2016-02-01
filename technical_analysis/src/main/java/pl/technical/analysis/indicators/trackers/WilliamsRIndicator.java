package pl.technical.analysis.indicators.trackers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


import org.assertj.core.util.BigDecimalComparator;
import pl.technical.analysis.Tickable;
import pl.technical.analysis.indicators.Indicator;

public class WilliamsRIndicator extends Indicator {

    public WilliamsRIndicator(List<Tickable> ticks, int period) {
        super(ticks, period);
    }

    @Override
    protected void calculateIndicator() {
        List<BigDecimal> closesPrices = super.getClosePrices();
        List<BigDecimal> minPrices = super.getMaxPrices();
        List<BigDecimal> maxPrices = super.getMinPrices();

        for (int i = 0; i < closesPrices.size() - super.period + 1; i++) {
            List<BigDecimal> periodlyClosesPrices = closesPrices.subList(i, super.period + i);
            List<BigDecimal> periodlyMinPrices = minPrices.subList(i, super.period + i);
            List<BigDecimal> periodlyMaxPrices = maxPrices.subList(i, super.period + i);

            BigDecimal maxOfThePeriod = periodlyMaxPrices.stream().max(new BigDecimalComparator()).get();
            BigDecimal minOfThePeriod = periodlyMinPrices.stream().min(new BigDecimalComparator()).get();
            BigDecimal currentClose = periodlyClosesPrices.get(period - 1);

            BigDecimal subtractClose = maxOfThePeriod.subtract(currentClose);
            BigDecimal subtractMin = maxOfThePeriod.subtract(minOfThePeriod);
            BigDecimal williamRIndicator = subtractClose.divide(subtractMin, 5, RoundingMode.HALF_UP).multiply(new BigDecimal("-100")).add(hundred);

            super.indicators.add(williamRIndicator.doubleValue());
        }

    }
}
