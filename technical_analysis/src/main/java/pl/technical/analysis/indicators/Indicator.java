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

    public Indicator(List<Tickable> ticks, int period) {
        if (period < 2) {
            throw new IllegalArgumentException("Period cannot be shorter than 2");
        }
        this.ticks = new AscendingSortedTicks(ticks);
        this.period = period;
        calculateIndicator();
    }

    protected List<BigDecimal> getClosePrices() {
        List<Tickable> ticks = this.ticks.getTicks();
        List<BigDecimal> closesPrices = ticks.stream().map(el -> el.getClosePrice()).collect(Collectors.toList());

        return closesPrices;
    }

    protected List<BigDecimal> getMaxPrices() {
        List<Tickable> ticks = this.ticks.getTicks();

        return ticks.stream().map(el -> el.getMaxPrice()).collect(Collectors.toList());
    }

    protected List<BigDecimal> getMinPrices() {
        List<Tickable> ticks = this.ticks.getTicks();

        return ticks.stream().map(el -> el.getMinPrice()).collect(Collectors.toList());
    }

    protected List<BigDecimal> getOpenPrices() {
        List<Tickable> ticks = this.ticks.getTicks();

        return ticks.stream().map(el -> el.getOpenPrice()).collect(Collectors.toList());
    }

    protected List<Long> getVolumens() {
        List<Tickable> ticks = this.ticks.getTicks();

        return ticks.stream().map(el -> el.getVolume()).collect(Collectors.toList());
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
        int indicatorsSize = indicators.size();
        if(shift > indicatorsSize || shift < 1) {
            throw new IllegalArgumentException("Invalid shift parameter");
        }

        return indicators.get(indicatorsSize - shift);
    }

    protected abstract void calculateIndicator();
}
