package com.gpw.radar.service.correlation;

import static java.util.EnumSet.complementOf;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.gpw.radar.domain.StockDetails;
import com.gpw.radar.domain.StockStatistic;
import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.repository.StockDetailsRepository;

@Service
public class CorrelationService {

	private final Logger log = LoggerFactory.getLogger(CorrelationService.class);

	@Inject
	private StockDetailsRepository stockDetailsRepository;

	private Correlator correlator;
	private int step;
	private boolean isComputing;
	private TreeSet<StockStatistic> correlationTreeSet;

	public TreeSet<StockStatistic> computeCorrelation(StockTicker correlationForTicker, int period, CorrelationType correlationType) {

		if (period != 10 && period != 30 && period != 60 && period != 90) {
			throw new IllegalArgumentException("Wrong period");
		}
		Objects.requireNonNull(correlationForTicker);
		Objects.requireNonNull(correlationType);

		correlator = getCorrelatorImpl(correlationType);
		correlationTreeSet = new TreeSet<StockStatistic>();
		final EnumSet<StockTicker> tickersToScan = complementOf(EnumSet.of(correlationForTicker));
		final double[] sourceClosePrices = getClosePrices(getContent(correlationForTicker, period));

		ExecutorService executor = Executors.newFixedThreadPool(2);

		for (StockTicker ticker : tickersToScan) {
			executor.submit(new CorrelationUtil(ticker, sourceClosePrices, period));
		}

		executor.shutdown();

		try {
			executor.awaitTermination(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.step = 0;
		this.isComputing = false;
		return correlationTreeSet;
	}

	private double[] getClosePrices(List<StockDetails> stockDetails) {
		double[] closePrices = new double[stockDetails.size()];

		int index = 0;
		for (StockDetails stds : stockDetails) {
			closePrices[index++] = stds.getClosePrice().doubleValue();
		}

		return closePrices;
	}

	private List<StockDetails> getContent(StockTicker ticker, int period) {
		PageRequest pageable = new PageRequest(0, period);
		Page<StockDetails> stockDetailsPaged = stockDetailsRepository.findByStockTickerOrderByDateDesc(ticker, pageable);
		List<StockDetails> stockDetails = stockDetailsPaged.getContent();
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
		}
		return null;
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

	class CorrelationUtil implements Runnable {

		StockTicker analysedTicker;
		double[] sourceClosePrices;
		int period;

		public CorrelationUtil(StockTicker analysedTicker, double[] sourceClosePrices, int period) {
			this.analysedTicker = analysedTicker;
			this.sourceClosePrices = sourceClosePrices;
			this.period = period;
		}

		@Override
		public void run() {
			double[] targetClosePrices = getClosePrices(getContent(analysedTicker, period));
			double correlation = correlator.correlate(sourceClosePrices, targetClosePrices);
			StockStatistic statistic = new StockStatistic(correlation, analysedTicker);
			correlationTreeSet.add(statistic);
			increaseStep();
		}
	}
}