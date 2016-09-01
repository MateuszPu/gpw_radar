package com.gpw.radar.service.parser.file.stockFiveMinutesDetails;


import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockFiveMinutesDetails;
import com.gpw.radar.domain.stock.StockFiveMinutesIndicators;

import java.io.InputStream;
import java.util.List;

public interface StockFiveMinutesDetailsParser {

    public List<StockFiveMinutesDetails> parseStockFiveMinutesDetails(Stock stock, InputStream st);

    public List<StockFiveMinutesDetails> fillEmptyTimeAndCumulativeVolume(List<StockFiveMinutesDetails> stockFiveMinutesDetailsList);

    public List<StockFiveMinutesIndicators> calculateIndicatorsFromDetails(List<StockFiveMinutesDetails> filledStockFiveMinutesDetails);

}
