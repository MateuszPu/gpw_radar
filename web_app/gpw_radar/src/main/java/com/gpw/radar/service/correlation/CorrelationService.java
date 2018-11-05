package com.gpw.radar.service.correlation;

import com.gpw.radar.domain.stock.StockStatistic;
import com.gpw.radar.elasticsearch.stockdetails.StockDetails;
import com.gpw.radar.dao.stockdetails.StockDetailsDAO;
import com.gpw.radar.repository.stock.StockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
public class CorrelationService {

    private final StockDetailsDAO stockDetailsDAO;
    private final StockRepository stockRepository;
    private final Logger logger = LoggerFactory.getLogger(CorrelationService.class);

    private int step;
    private boolean isComputing;

    @Autowired
    public CorrelationService(@Qualifier("stockDetailsElasticSearchDAO") StockDetailsDAO stockDetailsDAO, StockRepository stockRepository) {
        this.stockDetailsDAO = stockDetailsDAO;
        this.stockRepository = stockRepository;
    }

    public ResponseEntity<List<StockStatistic>> computeCorrelation(String correlationForTicker, int period,
                                                                      CorrelationType correlationType, int numberOfThreads) {
        if (validate(correlationForTicker, period, correlationType))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Correlator correlator = getCorrelatorImpl(correlationType);
        final List<StockStatistic> result = Collections.synchronizedList(new ArrayList());
        Set<String> tickersToScan = stockRepository.findAllTickerNotEquals(correlationForTicker);


        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        tickersToScan.forEach(ticker -> executor.submit(() -> result.add(computeCorrelation(correlationForTicker, ticker, period, correlator))));
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            logger.error("Error occurs {}", e);
        }
        Predicate<StockStatistic> isNull = Objects::isNull;
        List<StockStatistic> stockStatistics = result.stream().filter(isNull.negate()).sorted().collect(Collectors.toList());
        return new ResponseEntity<>(stockStatistics, HttpStatus.OK);
    }

    private boolean validate(String correlationForTicker, int period, CorrelationType correlationType) {
        if (period != 10 && period != 20 && period != 30 && period != 60 && period != 90
            && correlationForTicker != null && correlationType != null) {
            return true;
        }
        return false;
    }

    private StockStatistic computeCorrelation(String sourceTicker, String ticker, int period, Correlator correlator) {
        List<StockDetails> stockDetailsSource = getStockDetailsByTicker(sourceTicker, period);
        final double[] sourceClosePrices = stockDetailsSource.stream().mapToDouble(stockDetails -> stockDetails.getClosePrice().doubleValue()).toArray();
        LocalDate sourceEndDate = stockDetailsSource.stream().max(Comparator.comparing(StockDetails::getDate)).orElseThrow(RuntimeException::new).getDate();
        LocalDate sourceStartDate = stockDetailsSource.stream().min(Comparator.comparing(StockDetails::getDate)).orElseThrow(RuntimeException::new).getDate();

        double[] targetClosePrices = getClosePrices(getStockDetailsByTicker(ticker, period), sourceStartDate, sourceEndDate);
        if (targetClosePrices.length == 0) {
            return null;
        }
        double correlation = correlator.correlate(sourceClosePrices, targetClosePrices);
        return new StockStatistic(correlation, ticker);
    }

    private double[] getClosePrices(List<StockDetails> stockDetails, LocalDate sourceStartDate, LocalDate sourceEndDate) {
        double[] result = new double[0];
        LocalDate endDate = stockDetails.get(0).getDate();
        LocalDate startDate = stockDetails.get(stockDetails.size() - 1).getDate();

        if (startDate.equals(sourceStartDate) && endDate.equals(sourceEndDate)) {
            result = stockDetails.stream().mapToDouble(e -> e.getClosePrice().doubleValue()).toArray();
        }
        return result;
    }

    private List<StockDetails> getStockDetailsByTicker(String ticker, int period) {
        PageRequest pageable = new PageRequest(0, period);
        List<StockDetails> stockDetails = stockDetailsDAO.findByStockTickerOrderByDateDesc(ticker, pageable);
        return stockDetails;
    }

    private Correlator getCorrelatorImpl(CorrelationType correlationType) {
        switch (correlationType) {
            case KENDALLS:
                return new KendallsCorrelator();
            case PEARSONS:
                return new PearsonCorrelator();
            case SPEARMANS:
                return new SpearmansCorrelator();
            default:
                throw new IllegalArgumentException(String.format("Provided correlation type: %s is not handle in switch case", correlationType));
        }
    }

    public int getStep() {
        return step;
    }

    public boolean isComputing() {
        return isComputing;
    }

    private synchronized void increaseStep() {
        step++;
    }
}
