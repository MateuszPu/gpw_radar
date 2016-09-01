package com.gpw.radar.service.technical.indicators.sma;

import pl.technical.analysis.indicators.trackers.sma.SMACrossover;

public class SmaCrossoverAdapter implements Crossable {

    private SMACrossover smaCrossover;

    public SmaCrossoverAdapter(SMACrossover smaCrossover) {
        this.smaCrossover = smaCrossover;
    }

    @Override
    public boolean crossFromAbove() {
        return smaCrossover.isFastSMACrossSlowerFromAbove();
    }

    @Override
    public boolean crossFromBelow() {
        return smaCrossover.isFastSMACrossSlowerFromBellow();
    }
}
