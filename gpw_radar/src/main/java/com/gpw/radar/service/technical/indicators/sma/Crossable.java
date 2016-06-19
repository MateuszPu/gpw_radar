package com.gpw.radar.service.technical.indicators.sma;

/**
 * Created by mateusz on 16.06.2016.
 */
public interface Crossable {

    boolean crossFromAbove();
    boolean crossFromBelow();
}
