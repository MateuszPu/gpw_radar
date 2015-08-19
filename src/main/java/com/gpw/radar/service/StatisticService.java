package com.gpw.radar.service;

import static java.util.EnumSet.complementOf;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

	public TreeSet<StockStatistic> computePearsonCorrelation(StockTicker correlationForTicker, int period) {
		prepareVariables(correlationForTicker, period);

		ExecutorService executor = Executors.newFixedThreadPool(3);

		for (StockTicker ticker : tickersWithOutOneAnalysed) {
			executor.submit(new CorrelationStock(ticker));
		}

		executor.shutdown();

		try {
			executor.awaitTermination(1, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.step = 0;
		this.isComputing = false;
		return correlationTreeSet;
	}

	private void prepareVariables(StockTicker correlationForTicker, int period) {
		this.period = period;
		this.isComputing = true;

		correlationTreeSet = new TreeSet<>();
		pearsonsCorrelation = new PearsonsCorrelation();
		List<StockDetails> stockDetailsAnalysed = getContent(correlationForTicker, period);
		closePricesAnalysed = getClosePrices(stockDetailsAnalysed);
		EnumSet<StockTicker> tickers = complementOf(EnumSet.of(correlationForTicker));
		tickersWithOutOneAnalysed = new ArrayList<StockTicker>(tickers);
	}
	
	private void calculateCorrelation(StockTicker ticker) {
		double[] closePricesToCompare = getClosePrices(getContent(ticker, period));
		Double correlation = 0.0;
		if (closePricesToCompare.length == closePricesAnalysed.length) {
			correlation = pearsonsCorrelation.correlation(closePricesAnalysed, closePricesToCompare);
		}
		StockStatistic stockCorrelation = new StockStatistic(correlation, ticker);
		correlationTreeSet.add(stockCorrelation);
		increaseStep();
	}
	
	private double[] getClosePrices(List<StockDetails> stockDetails) {
		double[] closePrices = new double[stockDetails.size()];

		int index = 0;
		for (StockDetails stds : stockDetails) {
			closePrices[index++] = stds.getClosePrice().doubleValue();
		}

		return closePrices;
	}

	private List<StockDetails> getContent(StockTicker ticker, int size) {
		return stockDetailsRepository.findByStockTickerOrderByDateDesc(ticker, new PageRequest(1, size)).getContent();
	}

	private synchronized void increaseStep() {
		step++;
	}

	public int getStep() {
		return step;
	}

	public boolean isComputing() {
		return isComputing;
	}

	class CorrelationStock implements Runnable {
		StockTicker analysedTicker;

		public CorrelationStock(StockTicker analysedTicker) {
			this.analysedTicker = analysedTicker;
		}

		@Override
		public void run() {
			calculateCorrelation(analysedTicker);
		}
	}
}