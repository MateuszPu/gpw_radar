package com.gpw.radar.service.cron;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.joda.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gpw.radar.domain.Stock;
import com.gpw.radar.domain.StockDetails;
import com.gpw.radar.domain.StockIndicators;
import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.repository.StockDetailsRepository;
import com.gpw.radar.repository.StockRepository;
import com.gpw.radar.service.StockDetailsService;

@RestController
@RequestMapping("/api")
public class AutoUpdateDb {

	// TODO: CLEAN THE CODE! in final code delete request mapping, removed unused method
	// consider the way of calculate slope simple regression
	@Inject
	private StockRepository stockRepository;

	@Inject
	private StockDetailsService stockDetailsService;

	@Inject
	private StockDetailsRepository stockDetailsRepository;

	private boolean updating = false;
	private int step = 0;

	class ApplicationStatus {
		public boolean updating;
		public int step;

		public ApplicationStatus(boolean updating, int step) {
			this.updating = updating;
			this.step = step;
		}
	}

	@RequestMapping(value = "/is/updating", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ApplicationStatus updatingStatus() {
		return new ApplicationStatus(isUpdating() ,getStep());
	}

    @Transactional
    @RequestMapping(value = "/update/db", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public void updateData() throws IOException, InterruptedException {
		updating = true;
		step = 0;
		LocalDate date = stockDetailsService.findTopByDate().getDate();
		LocalDate stooqDate = stockDetailsService.getLastDateWig20FromStooq();

		if (!date.isEqual(stooqDate)) {
			updateDailyStockDetails(stooqDate);
			updateDailyStockIndicators();
		}
		updating = false;
	}

	private void updateDailyStockDetails(LocalDate wig20Date) throws IOException, InterruptedException {
		for (StockTicker element : StockTicker.values()) {
			Stock stock = stockRepository.findByTicker(element);
			stockDetailsService.updateStockDetails(stock, wig20Date);
			Thread.sleep(100);
			step++;
		}
	}

	private void updateDailyStockIndicators() {
		for (StockTicker element : StockTicker.values()) {
			Stock stock = stockRepository.findByTicker(element);
			Pageable top100th = new PageRequest(0, 100);
			Page<StockDetails> stockDetails = stockDetailsRepository.findByStockOrderByDateDesc(stock, top100th);
			int size = stockDetails.getContent().size();

			double[] openPrice = new double[size];
			double[] minPrice = new double[size];
			double[] maxPrice = new double[size];
			double[] closePrice = new double[size];
			double[] volume = new double[size];

			getDetails(stockDetails, size, openPrice, minPrice, maxPrice, closePrice, volume);
			double[] percentReturn = calculatePercentReturn(closePrice);

			calculateVolumeIndicators(stock, volume);

			double[] dataFor10DaysTrend = normalizeArray(Arrays.copyOfRange(closePrice, 0, 10));
			double[] dataFor30DaysTrend = normalizeArray(Arrays.copyOfRange(closePrice, 0, 30));
			double[] dataFor60DaysTrend = normalizeArray(Arrays.copyOfRange(closePrice, 0, 60));
			double[] dataFor90DaysTrend = normalizeArray(Arrays.copyOfRange(closePrice, 0, 90));

			double slopeSimpleRegression10 = calculateSlopeSimpleRegression(dataFor10DaysTrend);
			double slopeSimpleRegression30 = calculateSlopeSimpleRegression(dataFor30DaysTrend);
			double slopeSimpleRegression60 = calculateSlopeSimpleRegression(dataFor60DaysTrend);
			double slopeSimpleRegression90 = calculateSlopeSimpleRegression(dataFor90DaysTrend);

			// slopeSimpleRegression10 liczyc dla stop zwrotu
            stock.setStockIndicators(new StockIndicators());
			stock.getStockIndicators().setSlopeSimpleRegression10Days(slopeSimpleRegression10);
			stock.getStockIndicators().setSlopeSimpleRegression30Days(slopeSimpleRegression30);
			stock.getStockIndicators().setSlopeSimpleRegression60Days(slopeSimpleRegression60);
			stock.getStockIndicators().setSlopeSimpleRegression90Days(slopeSimpleRegression90);
			stock.setPercentReturn(new BigDecimal(percentReturn[0]));

			stockRepository.save(stock);
			step++;
		}
	}

    private double[] normalizeArray(double[] input){
        double[] normalized = new double[input.length];
        double max = StatUtils.max(input);
        double min = StatUtils.min(input);

        for(int i = 0; i < input.length; i++){
            normalized[i] = normalizeNumber(input[i], max, min);
        }
        ArrayUtils.reverse(normalized);
        return normalized;
    }

    private double normalizeNumber(double number, double max, double min) {
        return (number-min)/(max-min);
    }

    private void getDetails(Page<StockDetails> stockDetails, int size, double[] openPrice, double[] minPrice, double[] maxPrice, double[] closePrice,
			double[] volume) {
		for (int i = 0; i < size; i++) {
			StockDetails stockDetailsContent = stockDetails.getContent().get(i);
			openPrice[i] = stockDetailsContent.getOpenPrice().doubleValue();
			maxPrice[i] = stockDetailsContent.getMaxPrice().doubleValue();
			minPrice[i] = stockDetailsContent.getMinPrice().doubleValue();
			closePrice[i] = stockDetailsContent.getClosePrice().doubleValue();
			volume[i] = stockDetailsContent.getVolume();
		}
	}

	private double[] calculatePercentReturn(double[] closePrice) {
		int size = closePrice.length-1;

		double[] percentReturn = new double[size];
		for (int i = 0; i < size; i++) {
			percentReturn[i] = (closePrice[i] / closePrice[i + 1] - 1) * 100;
		}

		return percentReturn;
	}

	private void calculateVolumeIndicators(Stock stock, double[] volume) {
		stock.setAverageVolume10Days(new BigDecimal(StatUtils.mean(Arrays.copyOfRange(volume, 1, 11))));
		stock.setAverageVolume30Days(new BigDecimal(StatUtils.mean(Arrays.copyOfRange(volume, 1, 31))));
		stock.setVolumeRatio10(new BigDecimal(volume[0] / stock.getAverageVolume10Days().doubleValue()));
		stock.setVolumeRatio30(new BigDecimal(volume[0] / stock.getAverageVolume30Days().doubleValue()));
	}

	private double[] calculateDataForTrends(double[] closePrice, int size) {
		double[] dataForTrends = new double[size];
		dataForTrends[size - 1] = (closePrice[size - 1] / closePrice[size] - 1) * 100;
		for (int i = dataForTrends.length - 2; i >= 0; i--) {
			double percentReturn = (closePrice[i] / closePrice[i + 1] - 1) * 100;
			dataForTrends[i] = percentReturn + dataForTrends[i + 1];
		}
		ArrayUtils.reverse(dataForTrends);
		return dataForTrends;
	}

	private double calculateSlopeSimpleRegression(double[] dataForTrend) {
		SimpleRegression simpleRegerssion = new SimpleRegression();
		int size = dataForTrend.length - 1;
		for (int i = 0; i < size; i++) {
			simpleRegerssion.addData(i, dataForTrend[i]);
		}
		return simpleRegerssion.getSlope();
	}

	private double calculateAngleInDegress(double slopeSimpleRegression10) {
		return Math.atan2(slopeSimpleRegression10, 1) * 180 / Math.PI;
	}

	public boolean isUpdating() {
		return updating;
	}

	public void setUpdating(boolean isUpdating) {
		this.updating = isUpdating;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}
}
