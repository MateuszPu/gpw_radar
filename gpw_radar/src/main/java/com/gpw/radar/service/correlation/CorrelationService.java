package com.gpw.radar.service.correlation;

import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.domain.stock.StockStatistic;
import com.gpw.radar.repository.stock.StockDetailsRepository;
import com.gpw.radar.repository.stock.StockRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
public class CorrelationService {

    @Inject
    private StockDetailsRepository stockDetailsRepository;

    @Inject
    private StockRepository stockRepository;

    private LocalDate sourceStartDate;
    private LocalDate sourceEndDate;

    private int step;
    private boolean isComputing;

    public ResponseEntity<TreeSet<StockStatistic>> computeCorrelation(String correlationForTicker, int period, CorrelationType correlationType) {
        if (period != 10 && period != 20 && period != 30 && period != 60 && period != 90) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Objects.requireNonNull(correlationForTicker);
        Objects.requireNonNull(correlationType);

        this.step = 0;
        isComputing = true;

        Correlator correlator = getCorrelatorImpl(correlationType).get();
        TreeSet<StockStatistic> correlationTreeSet = new TreeSet<StockStatistic>();
        Set<String> tickersToScan = new HashSet<String>(stockRepository.findAllTickers());
        tickersToScan.remove(correlationForTicker);
        List<StockDetails> content = getContent(correlationForTicker, period);
        sourceEndDate = content.get(0).getDate();
        sourceStartDate = content.get(content.size() - 1).getDate();
        final double[] sourceClosePrices = getClosePrices(content);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        for (String ticker : tickersToScan) {
            executor.submit(() -> {
                double[] targetClosePrices = getClosePrices(getContent(ticker, period));
                if(targetClosePrices.length == 0) {
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
            e.printStackTrace();
        }

        this.step = 0;
        this.isComputing = false;
        return new ResponseEntity<>(correlationTreeSet, HttpStatus.OK);
    }

    private double[] getClosePrices(List<StockDetails> stockDetails) {
        double[] result = new double[0];
        LocalDate endDate = stockDetails.get(0).getDate();
        LocalDate startDate = stockDetails.get(stockDetails.size() - 1).getDate();

        if(startDate.equals(sourceStartDate) && endDate.equals(sourceEndDate)) {
            result = new double[stockDetails.size()];
            int index = 0;
            for (StockDetails stds : stockDetails) {
                result[index++] = stds.getClosePrice().doubleValue();
            }
        }
        return result;
    }

    private List<StockDetails> getContent(String ticker, int period) {
        PageRequest pageable = new PageRequest(0, period);
        Page<StockDetails> stockDetailsPaged = stockDetailsRepository.findByStockTickerOrderByDateDesc(ticker, pageable);
        List<StockDetails> stockDetails = stockDetailsPaged.getContent();
        return stockDetails;
    }

    private Optional<Correlator> getCorrelatorImpl(CorrelationType correlationType) {
        switch (correlationType) {
            case KENDALLS:
                return Optional.of(new KendallsCorrelator());
            case PEARSONS:
                return Optional.of(new PearsonCorrelator());
            case SPEARMANS:
                return Optional.of(new SpearmansCorrelator());
            default:
                return Optional.empty();
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
