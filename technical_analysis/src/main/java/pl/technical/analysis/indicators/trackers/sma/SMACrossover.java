package pl.technical.analysis.indicators.trackers.sma;

import pl.technical.analysis.Tickable;

import java.util.List;

public class SMACrossover {

    private SMAIndicator fasterSMA;
    private SMAIndicator slowerSMA;

    public SMACrossover(List<Tickable> ticks, int periodOfFasterSMA, int periodOfSlowerSMA) {
        if (periodOfFasterSMA > periodOfSlowerSMA) {
            this.fasterSMA = new SMAIndicator(ticks, periodOfFasterSMA);
            this.slowerSMA = new SMAIndicator(ticks, periodOfSlowerSMA);
        }
        if (periodOfFasterSMA < periodOfSlowerSMA) {
            this.fasterSMA = new SMAIndicator(ticks, periodOfSlowerSMA);
            this.slowerSMA = new SMAIndicator(ticks, periodOfFasterSMA);
        }
        if (periodOfFasterSMA == periodOfSlowerSMA) {
            throw new IllegalArgumentException("period of SMA should be different");
        }
        fasterSMA.calculateIndicator();
        slowerSMA.calculateIndicator();

    }


    public boolean isFastSMACrossSlowerFromAbove() {
        return slowerSMA.get(1) < fasterSMA.get(1) && slowerSMA.get(2) > fasterSMA.get(2);
    }

    public boolean isFastSMACrossSlowerFromBellow() {
        return slowerSMA.get(1) > fasterSMA.get(1) && slowerSMA.get(2) < fasterSMA.get(2);
    }
}