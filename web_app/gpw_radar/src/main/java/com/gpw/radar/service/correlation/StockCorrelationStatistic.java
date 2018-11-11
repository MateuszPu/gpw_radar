package com.gpw.radar.service.correlation;

import com.gpw.radar.elasticsearch.stockdetails.StockDetails;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public final class StockCorrelationStatistic {

    private String sourceStockTicker;
    private String stockTicker;
    private LocalDate startDate;
    private LocalDate endDate;
    private double[] sourcePrices;
    private double[] prices;
    private final Comparator<LocalDate> dateComparator = LocalDate::compareTo;

    private StockCorrelationStatistic() {
    }

    public static StockCorrelationStatistic builder() {
        return new StockCorrelationStatistic();
    }

    public StockCorrelationStatistic source(String sourceStockTicker) {
        this.sourceStockTicker = sourceStockTicker;
        return this;
    }

    public StockCorrelationStatistic compareTo(String stockTicker) {
        this.stockTicker = stockTicker;
        return this;
    }

    public StockCorrelationStatistic sourcePrices(Function<String, List<StockDetails>> stockDetailsFunction,
                                                  Function<StockDetails, Double> priceFunction) {
        List<StockDetails> stockDetails = stockDetailsFunction.apply(sourceStockTicker);
        startDate = stockDetails.stream()
            .map(StockDetails::getDate)
            .min(dateComparator)
            .orElse(LocalDate.MIN);

        endDate = stockDetails.stream()
            .map(StockDetails::getDate)
            .max(dateComparator.reversed())
            .orElse(LocalDate.MAX);

        sourcePrices = stockDetails.stream().map(priceFunction).mapToDouble(e -> e).toArray();
        return this;
    }

    public StockCorrelationStatistic destinationPrices(Function<String, List<StockDetails>> stockDetailsFunction,
                                                       Function<StockDetails, Double> priceFunction) {
        List<StockDetails> stockDetails = stockDetailsFunction.apply(stockTicker);

        LocalDate targetStartDate = stockDetails.stream()
            .map(StockDetails::getDate)
            .min(dateComparator)
            .orElse(LocalDate.MIN);

        LocalDate targetEndDate = stockDetails.stream()
            .map(StockDetails::getDate)
            .max(dateComparator.reversed())
            .orElse(LocalDate.MAX);
        if (startDate.equals(targetStartDate) && endDate.equals(targetEndDate)) {
            prices = stockDetails.stream().map(priceFunction).mapToDouble(price -> price).toArray();
        }
        return this;
    }

    public Optional<CorrelationResult> calculate(CorrelationType correlationType) {
        if (prices != null) {
            return Optional.of(new CorrelationResult(stockTicker, correlationType.calculate(sourcePrices, prices)));
        }
        return Optional.empty();
    }

}
