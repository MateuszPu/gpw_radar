package com.gpw.radar.service.technical.indicators;

import pl.technical.analysis.indicators.trackers.sma.PriceCrossSMA;

public class PriceCrossSmaAdapter implements Crossable {

    private PriceCrossSMA priceCrossSMA;

    public PriceCrossSmaAdapter(PriceCrossSMA priceCrossSMA){
        this.priceCrossSMA = priceCrossSMA;
    }

    @Override
    public boolean crossFromAbove() {
        return priceCrossSMA.isPriceCrossSMAFromAbove();
    }

    @Override
    public boolean crossFromBelow() {
        return priceCrossSMA.isPriceCrossSMAFromBellow();
    }
}
