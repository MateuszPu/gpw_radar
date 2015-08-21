package com.gpw.radar.service.correlation;

import static java.util.EnumSet.complementOf;

import java.util.EnumSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

	public TreeSet<StockStatistic> computeCorrelation(StockTicker correlationForTicker, int period, CorrelationType correlationType) {
		correlator = getCorrelatorImpl(correlationType, correlationForTicker, period, stockDetailsRepository);
		EnumSet<StockTicker> tickersToScan = complementOf(EnumSet.of(correlationForTicker));
		
		ExecutorService executor = Executors.newFixedThreadPool(5);

		for (StockTicker ticker : tickersToScan) {
			executor.submit(new CorrelationStock(ticker));
		}

		executor.shutdown();

		try {
			executor.awaitTermination(2, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.step = 0;
		this.isComputing = false;
		return correlator.getCorrelationTreeSet();
	}

	private Correlator getCorrelatorImpl(CorrelationType correlationType, StockTicker correlationForTicker, int period, StockDetailsRepository stockDetailsRepository2) {
		switch (correlationType) {
		case KENDALLS:
			return new KendallsCorrelator(correlationForTicker, period, stockDetailsRepository);
		case PEARSONS:
			return new PearsonCorrelator(correlationForTicker, period, stockDetailsRepository);
		case SPEARMANS:
			return new SpearmansCorrelator(correlationForTicker, period, stockDetailsRepository);
		}
		return null;
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
			correlator.compute(analysedTicker);
		}
	}
}