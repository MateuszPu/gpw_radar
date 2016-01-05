package pl.technical.analysis.indicators.trackers;

import pl.technical.analysis.Tickable;
import pl.technical.analysis.helpers.AscendingSortedTicks;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class SMAIndicators {

    private final AscendingSortedTicks ticks;
    private final int period;
    private List<Double> indicators = new ArrayList<>();

    public SMAIndicators(List<Tickable> ticks, int period) {
        if (period < 2) {
            throw new IllegalArgumentException("Period cannot be shorter than 2");
        }
        if (ticks.size() < period) {
            throw new IllegalArgumentException("Period cannot be longer than size of ticks");
        }
        this.ticks = new AscendingSortedTicks(ticks);
        this.period = period;
        calculateSMAIndicator();
    }

    private void calculateSMAIndicator() {
        List<Tickable> ticks = this.ticks.getTicks();
        List<BigDecimal> closesPrices = ticks.stream().map(el -> el.getClosePrice()).collect(Collectors.toList());
        BigDecimal sum = BigDecimal.valueOf(period);

        for (int i = 0; i < closesPrices.size() - period + 1; i++) {
            BigDecimal sumOfBigDecimals = closesPrices.subList(i, period + i)
                    .stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal average = sumOfBigDecimals.divide(sum, 2, RoundingMode.HALF_UP);
            indicators.add(average.doubleValue());
        }
    }

    public List<Double> getIndicators() {
        return new ArrayList<Double>(indicators);
    }

    public Double getLast() {
        return indicators.get(indicators.size() - 1);
    }
}
