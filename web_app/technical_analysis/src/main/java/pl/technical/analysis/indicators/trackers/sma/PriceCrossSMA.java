package pl.technical.analysis.indicators.trackers.sma;

import pl.technical.analysis.Tickable;

import java.util.List;

public class PriceCrossSMA {

    private SMAIndicator sma;

    public PriceCrossSMA(List<Tickable> ticks, int smaPeriod) {
        sma = new SMAIndicator(ticks, smaPeriod);
        sma.calculateIndicator();
    }

    public boolean isPriceCrossSMAFromAbove() {
        return sma.get(1) > sma.getClosePrice(1) && sma.get(1) < sma.getClosePrice(2);
    }

    public boolean isPriceCrossSMAFromBellow() {
        return sma.get(1) < sma.getClosePrice(1) && sma.get(1) > sma.getClosePrice(2);
    }
}
