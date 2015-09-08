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

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.gpw.radar.domain.StockDetails;
import com.gpw.radar.domain.StockStatistic;
import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.repository.StockDetailsRepository;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
public class CorrelationService {

	@Inject
	private StockDetailsRepository stockDetailsRepository;

	private int step;
	private boolean isComputing;

	public ResponseEntity<TreeSet<StockStatistic>> computeCorrelation(StockTicker correlationForTicker, int period, CorrelationType correlationType) {
		if (period != 10 && period != 20 && period != 30 && period != 60 && period != 90) {
			return new ResponseEntity<>(new TreeSet<StockStatistic>() , HttpStatus.BAD_REQUEST);
		}
		Objects.requireNonNull(correlationForTicker);
		Objects.requireNonNull(correlationType);
		
		this.step = 0;
		isComputing = true;

		Correlator correlator = getCorrelatorImpl(correlationType);
		TreeSet<StockStatistic> correlationTreeSet = new TreeSet<StockStatistic>();
		final EnumSet<StockTicker> tickersToScan = complementOf(EnumSet.of(correlationForTicker));
		final double[] sourceClosePrices = getClosePrices(getContent(correlationForTicker, period));

		ExecutorService executor = Executors.newFixedThreadPool(2);

		for (StockTicker ticker : tickersToScan) {
			executor.submit(() -> {
				double[] targetClosePrices = getClosePrices(getContent(ticker, period));
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
}