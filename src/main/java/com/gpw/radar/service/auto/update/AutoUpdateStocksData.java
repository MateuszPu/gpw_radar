package com.gpw.radar.service.auto.update;

import java.io.IOException;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gpw.radar.repository.StockDetailsRepository;
import com.gpw.radar.security.AuthoritiesConstants;
import com.gpw.radar.service.StockDetailsService;
import com.gpw.radar.service.WebParserService;
import com.gpw.radar.service.auto.update.status.ApplicationStatus;
import com.gpw.radar.service.auto.update.stockDetails.StockDetailsParser;

@RestController
@RequestMapping("/api")
@RolesAllowed(AuthoritiesConstants.ADMIN)
public class AutoUpdateStocksData {

	@Inject
	private StockDetailsService stockDetailsService;

	@Inject
	private WebParserService webParserService;

	@Inject
	private ApplicationStatus applicationStatus;
	
	@Inject
	private StockDetailsRepository configuratorRepository;

	@Inject
	private BeanFactory beanFactory;

	private StockDetailsParser stockDetailsParser;

	// @RequestMapping(value = "/status/step", method = RequestMethod.GET,
	// produces = MediaType.APPLICATION_JSON_VALUE)
	// public int getStepStatus() {
	// return applicationStatus.getStep();
	// }
	
	@RequestMapping(value = "/is/updating", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean isApplicationUpdating() {
		return applicationStatus.isUpdating();
	}

	@RequestMapping(value = "/update/db", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public void updateStockDetails() throws IOException, InterruptedException {
		LocalDate lastQuotedDateFromDataBase = stockDetailsService.findLastTopDate().getBody();
		LocalDate lastQuotedDateFromStooqWeb = webParserService.getLastDateWig20FromStooqWebsite();

		switch (configuratorRepository.findMethod()) {
		case GPW:
			stockDetailsParser = beanFactory.getBean("gpwParser", StockDetailsParser.class);
			break;
		case STOOQ:
			stockDetailsParser = beanFactory.getBean("stooqParser", StockDetailsParser.class);
			stockDetailsParser.setQutesDate(lastQuotedDateFromStooqWeb);
			break;
		}
		
		if (!lastQuotedDateFromDataBase.isEqual(lastQuotedDateFromStooqWeb)) {
			// updateDailyStockDetails(stooqDate);
//			 stockDetailsRepository.save(stockDetailsParser.getCurrentStockDetails());
			// updateDailyStockIndicators();
		}
	}

	// @Transactional
	// private void updateDailyStockIndicators() {
	// for (StockTicker element : StockTicker.values()) {
	// Stock stock = stockRepository.findByTicker(element);
	// StockIndicators stockIndicators =
	// stockIndicatorsRepository.findByStock(stock);
	// if (stockIndicators == null) {
	// stockIndicators = new StockIndicators();
	// }
	// Pageable top100th = new PageRequest(0, 100);
	// Page<StockDetails> stockDetails =
	// stockDetailsRepository.findByStockOrderByDateDesc(stock, top100th);
	// int size = stockDetails.getContent().size();
	//
	// double[] openPrice = new double[size];
	// double[] minPrice = new double[size];
	// double[] maxPrice = new double[size];
	// double[] closePrice = new double[size];
	// double[] volume = new double[size];
	//
	// getDetails(stockDetails, size, openPrice, minPrice, maxPrice, closePrice,
	// volume);
	// double[] percentReturn = calculatePercentReturn(closePrice);
	//
	// calculateVolumeIndicators(stockIndicators, volume);
	// double volumeValue =
	// stockIndicators.getAverageVolume30Days().doubleValue() * closePrice[0];
	// stockIndicators.setVolumeValue30Days(new BigDecimal(volumeValue));
	//
	// double[] dataFor10DaysTrend =
	// normalizeArray(Arrays.copyOfRange(closePrice, 0, 10));
	// double[] dataFor30DaysTrend =
	// normalizeArray(Arrays.copyOfRange(closePrice, 0, 30));
	// double[] dataFor60DaysTrend =
	// normalizeArray(Arrays.copyOfRange(closePrice, 0, 60));
	// double[] dataFor90DaysTrend =
	// normalizeArray(Arrays.copyOfRange(closePrice, 0, 90));
	//
	// double slopeSimpleRegression10 =
	// calculateSlopeSimpleRegression(dataFor10DaysTrend);
	// double slopeSimpleRegression30 =
	// calculateSlopeSimpleRegression(dataFor30DaysTrend);
	// double slopeSimpleRegression60 =
	// calculateSlopeSimpleRegression(dataFor60DaysTrend);
	// double slopeSimpleRegression90 =
	// calculateSlopeSimpleRegression(dataFor90DaysTrend);
	//
	// stockIndicators.setSlopeSimpleRegression10Days(slopeSimpleRegression10);
	// stockIndicators.setSlopeSimpleRegression30Days(slopeSimpleRegression30);
	// stockIndicators.setSlopeSimpleRegression60Days(slopeSimpleRegression60);
	// stockIndicators.setSlopeSimpleRegression90Days(slopeSimpleRegression90);
	// stockIndicators.setPercentReturn(new BigDecimal(percentReturn[0]));
	// stockIndicators.setStock(stock);
	//
	// stockIndicatorsRepository.save(stockIndicators);
	// }
	// }
	//
	// private double[] normalizeArray(double[] input) {
	// double[] normalized = new double[input.length];
	// double max = StatUtils.max(input);
	// double min = StatUtils.min(input);
	//
	// for (int i = 0; i < input.length; i++) {
	// normalized[i] = normalizeData(input[i], max, min);
	// }
	// ArrayUtils.reverse(normalized);
	// return normalized;
	// }
	//
	// private double normalizeData(double number, double max, double min) {
	// return (number - min) / (max - min) * 100;
	// }
	//
	// private void getDetails(Page<StockDetails> stockDetails, int size,
	// double[] openPrice, double[] minPrice, double[] maxPrice, double[]
	// closePrice, double[] volume) {
	// for (int i = 0; i < size; i++) {
	// StockDetails stockDetailsContent = stockDetails.getContent().get(i);
	// openPrice[i] = stockDetailsContent.getOpenPrice().doubleValue();
	// maxPrice[i] = stockDetailsContent.getMaxPrice().doubleValue();
	// minPrice[i] = stockDetailsContent.getMinPrice().doubleValue();
	// closePrice[i] = stockDetailsContent.getClosePrice().doubleValue();
	// volume[i] = stockDetailsContent.getVolume();
	// }
	// }
	//
	// private double[] calculatePercentReturn(double[] closePrice) {
	// int size = closePrice.length - 1;
	//
	// double[] percentReturn = new double[size];
	// for (int i = 0; i < size; i++) {
	// percentReturn[i] = (closePrice[i] / closePrice[i + 1] - 1) * 100;
	// }
	//
	// return percentReturn;
	// }
	//
	// private void calculateVolumeIndicators(StockIndicators stockIndicators,
	// double[] volume) {
	// stockIndicators.setAverageVolume10Days(new
	// BigDecimal(StatUtils.mean(Arrays.copyOfRange(volume, 1, 11))));
	// stockIndicators.setAverageVolume30Days(new
	// BigDecimal(StatUtils.mean(Arrays.copyOfRange(volume, 1, 31))));
	// stockIndicators.setVolumeRatio10(new BigDecimal(volume[0] /
	// stockIndicators.getAverageVolume10Days().doubleValue()));
	// stockIndicators.setVolumeRatio30(new BigDecimal(volume[0] /
	// stockIndicators.getAverageVolume30Days().doubleValue()));
	// }
	//
	// private double calculateSlopeSimpleRegression(double[] dataForTrend) {
	// SimpleRegression simpleRegerssion = new SimpleRegression();
	// int size = dataForTrend.length - 1;
	// for (int i = 0; i < size; i++) {
	// simpleRegerssion.addData(i, dataForTrend[i]);
	// }
	// return simpleRegerssion.getSlope();
	// }
}
