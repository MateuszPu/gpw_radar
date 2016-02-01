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
        List<BigDecimal> closesPrices = ticks.stream().map(el -> el.getMaxPrice()).collect(Collectors.toList());

        return closesPrices;
    }

    protected List<BigDecimal> getMinPrices() {
        List<Tickable> ticks = this.ticks.getTicks();
        List<BigDecimal> closesPrices = ticks.stream().map(el -> el.getMinPrice()).collect(Collectors.toList());

        return closesPrices;
    }

    protected List<BigDecimal> getOpenPrices() {
        List<Tickable> ticks = this.ticks.getTicks();
        List<BigDecimal> closesPrices = ticks.stream().map(el -> el.getOpenPrice()).collect(Collectors.toList());

        return closesPrices;
    }

    protected List<Long> getVolumens() {
        List<Tickable> ticks = this.ticks.getTicks();
        List<Long> closesPrices = ticks.stream().map(el -> el.getVolume()).collect(Collectors.toList());

        return closesPrices;
    }

    public List<Double> getIndicators() {
        return new ArrayList<Double>(indicators);
    }

    public double getLast() {
        return indicators.get(indicators.size() - 1);
    }

    protected abstract void calculateIndicator();
}
