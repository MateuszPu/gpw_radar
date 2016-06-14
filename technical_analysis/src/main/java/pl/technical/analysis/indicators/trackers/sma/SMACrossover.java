package pl.technical.analysis.indicators.trackers.sma;

import pl.technical.analysis.Tickable;

import java.util.List;

public class SMACrossover {

    private SMAIndicator fasterSMA;
    private SMAIndicator slowerSMA;

    public SMACrossover(List<Tickable> ticks, int fasterSMAPeriod, int slowerSMAPeriod) {
        if (fasterSMAPeriod > slowerSMAPeriod) {
            this.fasterSMA = new SMAIndicator(ticks, fasterSMAPeriod);
            this.slowerSMA = new SMAIndicator(ticks, slowerSMAPeriod);
        }
        if (fasterSMAPeriod < slowerSMAPeriod) {
            this.fasterSMA = new SMAIndicator(ticks, slowerSMAPeriod);
            this.slowerSMA = new SMAIndicator(ticks, fasterSMAPeriod);
        }
        if (fasterSMAPeriod == slowerSMAPeriod) {
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