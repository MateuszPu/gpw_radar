package pl.technical.analysis.indicators;

import pl.technical.analysis.Tickable;
import pl.technical.analysis.helpers.AscendingSortedTicks;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Indicator {

    protected final BigDecimal hundred = BigDecimal.valueOf(100);
    protected final AscendingSortedTicks ticks;
    protected final int period;
    protected List<Double> indicators = new ArrayList<>();

    protected Indicator(List<Tickable> ticks, int period) {
        if (period < 2) {
            throw new IllegalArgumentException("Period cannot be shorter than 2");
        }
        this.ticks = new AscendingSortedTicks(ticks);
        this.period = period;
        calculateIndicator();
    }

    protected List<BigDecimal> getClosePrices() {
        return this.ticks.getTicks().stream().map(el -> el.getClosePrice()).collect(Collectors.toList());
    }

    public double getClosePrice(int shift) {
        validateShiftLength(shift);
        return getClosePrices().get(ticks.size() - shift).doubleValue();
    }

    protected List<BigDecimal> getMaxPrices() {
        return this.ticks.getTicks().stream().map(el -> el.getMaxPrice()).collect(Collectors.toList());
    }

    public double getMaxPrice(int shift) {
        validateShiftLength(shift);
        return getMaxPrices().get(ticks.size() - shift).doubleValue();
    }

    protected List<BigDecimal> getMinPrices() {
        return this.ticks.getTicks().stream().map(el -> el.getMinPrice()).collect(Collectors.toList());
    }

    public double getMinPrice(int shift) {
        validateShiftLength(shift);
        return getMinPrices().get(ticks.size() - shift).doubleValue();
    }

    protected List<BigDecimal> getOpenPrices() {
        return this.ticks.getTicks().stream().map(el -> el.getOpenPrice()).collect(Collectors.toList());
    }

    public double getOpenPrice(int shift) {
        validateShiftLength(shift);
        return getOpenPrices().get(ticks.size() - shift).doubleValue();
    }

    protected List<Long> getVolumes() {
        return this.ticks.getTicks().stream().map(el -> el.getVolume()).collect(Collectors.toList());
    }

    public long getVolumePrice(int shift) {
        validateShiftLength(shift);
        return getVolumes().get(ticks.size() - shift).longValue();
    }

    public List<Double> getIndicators() {
        return new ArrayList<Double>(indicators);
    }

    public double getLast() {
        return get(1);
    }

    /**
     * @param shift number of indicators back in time from the fresh one. shift 1 - return last, shift 2 -return indicator before last etc...
     * @return value of the indicator base on shift parameter
     */
    public double get(int shift) {
        validateShiftLength(shift);
        return indicators.get(indicators.size() - shift);
    }

    private void validateShiftLength(int shift) {
        if (shift > indicators.size() || shift < 1) {
            throw new IllegalArgumentException("Invalid shift parameter");
        }
    }

    protected abstract void calculateIndicator();
}
