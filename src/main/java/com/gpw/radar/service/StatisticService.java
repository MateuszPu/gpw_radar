package com.gpw.radar.service;

import static java.util.EnumSet.complementOf;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.TreeSet;

import javax.inject.Inject;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.gpw.radar.domain.StockDetails;
import com.gpw.radar.domain.StockStatistic;
import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.repository.StockDetailsRepository;

@Service
public class StatisticService {

    @Inject
    private StockDetailsRepository stockDetailsRepository;

    private List<StockTicker> tickersWithOutOneAnalysed;
    private TreeSet<StockStatistic> correlationTreeSet;
    private double[] closePricesAnalysed;
    private int period;
    private PearsonsCorrelation pearsonsCorrelation;
    private int step;
    private boolean isComputing;

    public TreeSet<StockStatistic> computePearsonCorrelation(StockTicker analysedTicker, int period) {
        this.period = period;
        this.isComputing = true;

        correlationTreeSet = new TreeSet<>();
        pearsonsCorrelation = new PearsonsCorrelation();
        List<StockDetails> stockDetailsAnalysed = getContent(analysedTicker, period);
        closePricesAnalysed = getClosePrices(stockDetailsAnalysed);
        EnumSet<StockTicker> tickers = complementOf(EnumSet.of(analysedTicker));
        tickersWithOutOneAnalysed = new ArrayList<StockTicker>(tickers);

        Thread threadOne = new Thread(new Runnable() {
            public void run() {
                firstPartTickers();
            }
        });

        Thread threadTwo = new Thread(new Runnable() {
            public void run() {
                secondPartTickers();
            }
        });

        threadOne.start();
        threadTwo.start();

        try {
            threadOne.join();
            threadTwo.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.step = 0;
        this.isComputing = false;
        return correlationTreeSet;
    }

    private void firstPartTickers() {
        calculateTickers(0, 2);
    }

    private void secondPartTickers() {
        calculateTickers(1, 2);
    }

    private void calculateTickers(int start, int increment) {
        for (int index = start; index < tickersWithOutOneAnalysed.size(); index += increment) {
            calculateCorrelation(index);
        }
    }

    private void calculateCorrelation(int index) {
        StockTicker ticker = StockTicker.valueOf(tickersWithOutOneAnalysed.get(index).name());
        double[] closePricesToCompare = getClosePrices(getContent(ticker, period));
        Double correlation = 0.00;
        if(closePricesToCompare.length == closePricesAnalysed.length){
            correlation = pearsonsCorrelation.correlation(closePricesAnalysed, closePricesToCompare);
        }
        StockStatistic stockCorrelation = new StockStatistic(correlation, ticker);
        correlationTreeSet.add(stockCorrelation);
        increaseStep();
    }

    private List<StockDetails> getContent(StockTicker ticker, int size) {
        return stockDetailsRepository.findByStockTickerOrderByDateDesc(ticker, new PageRequest(1, size)).getContent();
    }

    private double[] getClosePrices(List<StockDetails> stockDetails) {
        double[] closePrices = new double[stockDetails.size()];

        int index = 0;
        for (StockDetails stds : stockDetails) {
            closePrices[index++] = stds.getClosePrice().doubleValue();
        }

        return closePrices;
    }

    private synchronized void increaseStep(){
        step++;
    }

    public int getStep() {
        return step;
    }

    public boolean isComputing() {
        return isComputing;
    }
}