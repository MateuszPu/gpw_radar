package com.gpw.radar.service.correlation;

import com.gpw.radar.domain.stock.StockStatistic;
import com.gpw.radar.elasticsearch.domain.stockdetails.StockDetails;
import com.gpw.radar.elasticsearch.service.stockdetails.StockDetailsDAO;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

    public ResponseEntity<TreeSet<StockStatistic>> computeCorrelation(String correlationForTicker, int period,
                                                                      CorrelationType correlationType, int numberOfThreads) {
        if (period != 10 && period != 20 && period != 30 && period != 60 && period != 90
            && correlationForTicker != null && correlationType != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        this.step = 0;
        isComputing = true;

        Correlator correlator = getCorrelatorImpl(correlationType);
        TreeSet<StockStatistic> correlationTreeSet = new TreeSet<>();
        Set<String> tickersToScan = new HashSet<>(stockRepository.findAllTickers());
        tickersToScan.remove(correlationForTicker);
        List<StockDetails> stockDetailsSource = getStockDetailsByTicker(correlationForTicker, period);
        LocalDate sourceEndDate = stockDetailsSource.get(0).getDate();
        LocalDate sourceStartDate = stockDetailsSource.get(stockDetailsSource.size() - 1).getDate();
        final double[] sourceClosePrices = stockDetailsSource.stream().mapToDouble(e -> e.getClosePrice().doubleValue()).toArray();

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

        for (String ticker : tickersToScan) {
            executor.submit(() -> {
                double[] targetClosePrices = getClosePrices(getStockDetailsByTicker(ticker, period), sourceStartDate, sourceEndDate);
                if (targetClosePrices.length == 0) {
                    return;
                }
                double correlation = correlator.correlate(sourceClosePrices, targetClosePrices);
                StockStatistic statistic = new StockStatistic(correlation, ticker);
                correlationTreeSet.add(statistic);
                increaseStep();
            });
        }
        executor.shutdown();

        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            logger.error("Error occurs {}", e);
        }

        this.step = 0;
        this.isComputing = false;
        return new ResponseEntity<>(correlationTreeSet, HttpStatus.OK);
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
