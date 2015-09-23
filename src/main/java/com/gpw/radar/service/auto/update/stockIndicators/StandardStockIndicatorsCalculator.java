//package com.gpw.radar.service.auto.update.stockIndicators;
//
//import java.math.BigDecimal;
//import java.util.Arrays;
//
//import javax.transaction.Transactional;
//
//import org.apache.commons.lang.ArrayUtils;
//import org.apache.commons.math3.stat.StatUtils;
//import org.apache.commons.math3.stat.regression.SimpleRegression;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//
//import com.gpw.radar.domain.Stock;
//import com.gpw.radar.domain.StockDetails;
//import com.gpw.radar.domain.StockIndicators;
//import com.gpw.radar.domain.enumeration.StockTicker;
//
//public class StandardStockIndicatorsCalculator {
//
//	@Transactional
//	private void updateDailyStockIndicators() {
//		for (StockTicker element : StockTicker.values()) {
//			Stock stock = stockRepository.findByTicker(element);
//			StockIndicators stockIndicators = stockIndicatorsRepository.findByStock(stock);
//			if (stockIndicators == null) {
//				stockIndicators = new StockIndicators();
//			}
//			Pageable top100th = new PageRequest(0, 100);
//			Page<StockDetails> stockDetails = stockDetailsRepository.findByStockOrderByDateDesc(stock, top100th);
//			int size = stockDetails.getContent().size();
//
//			double[] openPrice = new double[size];
//			double[] minPrice = new double[size];
//			double[] maxPrice = new double[size];
//			double[] closePrice = new double[size];
//			double[] volume = new double[size];
//
//			getDetails(stockDetails, size, openPrice, minPrice, maxPrice, closePrice, volume);
//			double[] percentReturn = calculatePercentReturn(closePrice);
//
//			calculateVolumeIndicators(stockIndicators, volume);
//			double volumeValue = stockIndicators.getAverageVolume30Days().doubleValue() * closePrice[0];
//			stockIndicators.setVolumeValue30Days(new BigDecimal(volumeValue));
//
//			double[] dataFor10DaysTrend = normalizeArray(Arrays.copyOfRange(closePrice, 0, 10));
//			double[] dataFor30DaysTrend = normalizeArray(Arrays.copyOfRange(closePrice, 0, 30));
//			double[] dataFor60DaysTrend = normalizeArray(Arrays.copyOfRange(closePrice, 0, 60));
//			double[] dataFor90DaysTrend = normalizeArray(Arrays.copyOfRange(closePrice, 0, 90));
//
//			double slopeSimpleRegression10 = calculateSlopeSimpleRegression(dataFor10DaysTrend);
//			double slopeSimpleRegression30 = calculateSlopeSimpleRegression(dataFor30DaysTrend);
//			double slopeSimpleRegression60 = calculateSlopeSimpleRegression(dataFor60DaysTrend);
//			double slopeSimpleRegression90 = calculateSlopeSimpleRegression(dataFor90DaysTrend);
//
//			stockIndicators.setSlopeSimpleRegression10Days(slopeSimpleRegression10);
//			stockIndicators.setSlopeSimpleRegression30Days(slopeSimpleRegression30);
//			stockIndicators.setSlopeSimpleRegression60Days(slopeSimpleRegression60);
//			stockIndicators.setSlopeSimpleRegression90Days(slopeSimpleRegression90);
//			stockIndicators.setPercentReturn(new BigDecimal(percentReturn[0]));
//			stockIndicators.setStock(stock);
//
//			stockIndicatorsRepository.save(stockIndicators);
//		}
//	}
//
//	private double[] normalizeArray(double[] input) {
//		double[] normalized = new double[input.length];
//		double max = StatUtils.max(input);
//		double min = StatUtils.min(input);
//
//		for (int i = 0; i < input.length; i++) {
//			normalized[i] = normalizeData(input[i], max, min);
//		}
//		ArrayUtils.reverse(normalized);
//		return normalized;
//	}
//
//	private double normalizeData(double number, double max, double min) {
//		return (number - min) / (max - min) * 100;
//	}
//
//	private void getDetails(Page<StockDetails> stockDetails, int size, double[] openPrice, double[] minPrice, double[] maxPrice, double[] closePrice, double[] volume) {
//		for (int i = 0; i < size; i++) {
//			StockDetails stockDetailsContent = stockDetails.getContent().get(i);
//			openPrice[i] = stockDetailsContent.getOpenPrice().doubleValue();
//			maxPrice[i] = stockDetailsContent.getMaxPrice().doubleValue();
//			minPrice[i] = stockDetailsContent.getMinPrice().doubleValue();
//			closePrice[i] = stockDetailsContent.getClosePrice().doubleValue();
//			volume[i] = stockDetailsContent.getVolume();
//		}
//	}
//
//	private double[] calculatePercentReturn(double[] closePrice) {
//		int size = closePrice.length - 1;
//
//		double[] percentReturn = new double[size];
//		for (int i = 0; i < size; i++) {
//			percentReturn[i] = (closePrice[i] / closePrice[i + 1] - 1) * 100;
//		}
//
//		return percentReturn;
//	}
//
//	private void calculateVolumeIndicators(StockIndicators stockIndicators, double[] volume) {
//		stockIndicators.setAverageVolume10Days(new BigDecimal(StatUtils.mean(Arrays.copyOfRange(volume, 1, 11))));
//		stockIndicators.setAverageVolume30Days(new BigDecimal(StatUtils.mean(Arrays.copyOfRange(volume, 1, 31))));
//		stockIndicators.setVolumeRatio10(new BigDecimal(volume[0] / stockIndicators.getAverageVolume10Days().doubleValue()));
//		stockIndicators.setVolumeRatio30(new BigDecimal(volume[0] / stockIndicators.getAverageVolume30Days().doubleValue()));
//	}
//
//	private double calculateSlopeSimpleRegression(double[] dataForTrend) {
//		SimpleRegression simpleRegerssion = new SimpleRegression();
//		int size = dataForTrend.length - 1;
//		for (int i = 0; i < size; i++) {
//			simpleRegerssion.addData(i, dataForTrend[i]);
//		}
//		return simpleRegerssion.getSlope();
//	}
//}
