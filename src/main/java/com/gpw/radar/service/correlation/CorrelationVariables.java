package com.gpw.radar.service.correlation;

import static java.util.EnumSet.complementOf;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.TreeSet;

import org.springframework.data.domain.PageRequest;

import com.gpw.radar.domain.StockDetails;
import com.gpw.radar.domain.StockStatistic;
import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.repository.StockDetailsRepository;

public abstract class CorrelationVariables {

	protected StockDetailsRepository stockDetailsRepository;
	protected List<StockTicker> tickersWithOutOneAnalysed;
	protected TreeSet<StockStatistic> correlationTreeSet;
	protected double[] sourceClosePrices;
	protected final int period;

	public CorrelationVariables(StockTicker correlationForTicker, int period, StockDetailsRepository stockDetailsRepository) {
		this.stockDetailsRepository = stockDetailsRepository;
		this.period = period;
		this.correlationTreeSet = new TreeSet<>();
		List<StockDetails> stockDetailsToAnalysed = getContent(correlationForTicker);
		this.sourceClosePrices = getClosePrices(stockDetailsToAnalysed);
		EnumSet<StockTicker> tickersToScan = complementOf(EnumSet.of(correlationForTicker));
		this.tickersWithOutOneAnalysed = new ArrayList<StockTicker>(tickersToScan);
	}

	public List<StockTicker> getTickersWithOutOneAnalysed() {
		return tickersWithOutOneAnalysed;
	}

	public TreeSet<StockStatistic> getCorrelationTreeSet() {
		return correlationTreeSet;
	}

	public void setCorrelationTreeSet(TreeSet<StockStatistic> correlationTreeSet) {
		this.correlationTreeSet = correlationTreeSet;
	}

	public double[] getClosePricesToAnalysed() {
		return sourceClosePrices;
	}

	protected double[] getClosePrices(List<StockDetails> stockDetails) {
		double[] closePrices = new double[stockDetails.size()];

		int index = 0;
		for (StockDetails stds : stockDetails) {
			closePrices[index++] = stds.getClosePrice().doubleValue();
		}

		return closePrices;
	}

	protected List<StockDetails> getContent(StockTicker ticker) {
		return stockDetailsRepository.findByStockTickerOrderByDateDesc(ticker, new PageRequest(1, period)).getContent();
	}
}
