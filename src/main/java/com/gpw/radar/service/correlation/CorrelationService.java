package com.gpw.radar.service.correlation;

import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.gpw.radar.domain.StockStatistic;
import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.repository.StockDetailsRepository;

@Service
public class CorrelationService {

	@Inject
	private StockDetailsRepository stockDetailsRepository;

	private Correlation correlation;
	private int step;
	private boolean isComputing;

	public TreeSet<StockStatistic> computeCorrelation(StockTicker correlationForTicker, int period, CorrelationType correlationType) {
		correlation = getCorrelationImpl(correlationType, correlationForTicker, period, stockDetailsRepository);
		ExecutorService executor = Executors.newFixedThreadPool(5);

		int index = 0;
		for (StockTicker ticker : correlation.getTickersWithOutOneAnalysed()) {
			executor.submit(new CorrelationStock(ticker, index));
			index ++;
		}

		executor.shutdown();

		try {
			executor.awaitTermination(1, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.step = 0;
		this.isComputing = false;
		return correlation.getCorrelationTreeSet();
	}

	private Correlation getCorrelationImpl(CorrelationType correlationType, StockTicker correlationForTicker, int period, StockDetailsRepository stockDetailsRepository2) {
		switch (correlationType) {
		case KENDALLS:
			return new KendallsCorrelationImpl(correlationForTicker, period, stockDetailsRepository);
		case PEARSONS:
			return new PearsonCorrelationImpl(correlationForTicker, period, stockDetailsRepository);
		case SPEARMANS:
			return new SpearmansCorrelationImpl(correlationForTicker, period, stockDetailsRepository);
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
		int index;

		public CorrelationStock(StockTicker analysedTicker, int index) {
			this.analysedTicker = analysedTicker;
			this.index = index;
		}

		@Override
		public void run() {
			System.out.println(index+"."+" Start computing correlation for: [" + analysedTicker +"]");
			correlation.compute(analysedTicker);
			System.out.println("Fnish computing correlation for: [" + analysedTicker +"]");
		}
	}
}