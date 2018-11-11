package com.gpw.radar.service.correlation;

import com.gpw.radar.dao.stockdetails.StockDetailsDAO;
import com.gpw.radar.elasticsearch.stockdetails.StockDetails;
import com.gpw.radar.repository.stock.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
public class CorrelationService {

    private final StockDetailsDAO stockDetailsDAO;
    private final StockRepository stockRepository;

    @Autowired
    public CorrelationService(@Qualifier("stockDetailsElasticSearchDAO") StockDetailsDAO stockDetailsDAO,
                              StockRepository stockRepository) {
        this.stockDetailsDAO = stockDetailsDAO;
        this.stockRepository = stockRepository;
    }

    public ResponseEntity<List<CorrelationResult>> computeCorrelation(String correlationForTicker, int period,
                                                                              CorrelationType correlationType) {
        if (validate(correlationForTicker, period, correlationType)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Set<String> tickersToScan = stockRepository.findAllTickerNotEquals(correlationForTicker);
        List<CorrelationResult> result = tickersToScan.stream()
            .parallel()
            .map(targetTicker -> calculateCorrelationFor(targetTicker, correlationForTicker, period, correlationType))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .sorted()
            .collect(Collectors.toList());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private Optional<CorrelationResult> calculateCorrelationFor(String targetTicker, String sourceTicker, int period, CorrelationType correlationType) {
        Function<String, List<StockDetails>> getStockDetails = ticker -> stockDetailsDAO.findByStockTickerOrderByDateDesc(ticker, new PageRequest(0, period));
        Function<StockDetails, Double> getPriceToCompare = stockDetails -> stockDetails.getClosePrice().doubleValue();
        return StockCorrelationStatistic.builder()
            .source(sourceTicker)
            .sourcePrices(getStockDetails, getPriceToCompare)
            .compareTo(targetTicker)
            .destinationPrices(getStockDetails, getPriceToCompare)
            .calculate(correlationType);
    }

    private boolean validate(String correlationForTicker, int period, CorrelationType correlationType) {
        if (period != 10 && period != 20 && period != 30 && period != 60 && period != 90
            && correlationForTicker != null && correlationType != null) {
            return true;
        }
        return false;
    }
}
