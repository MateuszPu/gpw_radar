package com.gpw.radar.service.auto.update.stockFiveMinutesDetails;

import com.gpw.radar.domain.stock.StockFiveMinutesDetails;

import java.time.LocalTime;
import java.util.List;

public interface stockFiveMinutesDetailsParser {

    List<StockFiveMinutesDetails> parseFiveMinutesStockDetails(LocalTime lookingTime);
}
