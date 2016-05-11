package com.gpw.radar.service.auto.update.stockDetails.indicators;

import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.domain.stock.StockIndicators;
import com.gpw.radar.repository.stock.StockDetailsRepository;
import com.gpw.radar.repository.stock.StockIndicatorsRepository;
import com.gpw.radar.repository.stock.StockRepository;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component("standardStockIndicatorsCalculator")
public class StandardStockIndicatorsCalculator implements StockIndicatorsCalculator {

    @Inject
    private StockDetailsRepository stockDetailsRepository;

    @Inject
    private StockRepository stockRepository;

    @Inject
    private StockIndicatorsRepository stockIndicatorsRepository;

    private double[] openPrice;
    private double[] minPrice;
    private double[] maxPrice;
    private double[] closePrice;
    private double[] volume;

    @Override
    public List<StockIndicators> calculateCurrentStockIndicators() {
        List<StockIndicators> stockIndicators = new ArrayList<StockIndicators>();
        for (String element : stockRepository.findAllTickers()) {
            stockIndicators.add(calculateStockIndicator(element));
        }
        return stockIndicators;
    }

    private StockIndicators calculateStockIndicator(String stockTicker) {
        Stock stock = stockRepository.findByTicker(stockTicker);

        StockIndicators stockIndicators = stockIndicatorsRepository.findByStock(stock);
        if (stockIndicators == null) {
            stockIndicators = new StockIndicators();
        }
        prepareVeriables(stock);

        stockIndicators.setPercentReturn(calculatePercentReturn(closePrice));
        stockIndicators.setAverageVolume10Days(calculateAverageVolume(volume, 10));
        stockIndicators.setAverageVolume30Days(calculateAverageVolume(volume, 30));
        stockIndicators.setVolumeRatio10(calculateVolumeRation(volume, 10));
        stockIndicators.setVolumeRatio30(calculateVolumeRation(volume, 30));
        stockIndicators.setVolumeValue30Days(calculateVolumeValue(volume, closePrice, 30));
        stockIndicators.setSlopeSimpleRegression10Days(calculateSlopeSimpleRegression(closePrice, 10));
        stockIndicators.setSlopeSimpleRegression30Days(calculateSlopeSimpleRegression(closePrice, 30));
        stockIndicators.setSlopeSimpleRegression60Days(calculateSlopeSimpleRegression(closePrice, 60));
        stockIndicators.setSlopeSimpleRegression90Days(calculateSlopeSimpleRegression(closePrice, 90));
        stockIndicators.setStock(stock);

        return stockIndicators;
    }

    private void prepareVeriables(Stock stock) {
        Pageable top100th = new PageRequest(0, 100);
        Page<StockDetails> stockDetails = stockDetailsRepository.findByStockOrderByDateDesc(stock, top100th);
        int size = stockDetails.getContent().size();

        openPrice = new double[size];
        minPrice = new double[size];
        maxPrice = new double[size];
        closePrice = new double[size];
        volume = new double[size];
        getStockDetails(stockDetails, size);
    }

    private void getStockDetails(Page<StockDetails> stockDetails, int size) {
        for (int i = 0; i < size; i++) {
            StockDetails stockDetailsContent = stockDetails.getContent().get(i);
            openPrice[i] = stockDetailsContent.getOpenPrice().doubleValue();
            maxPrice[i] = stockDetailsContent.getMaxPrice().doubleValue();
            minPrice[i] = stockDetailsContent.getMinPrice().doubleValue();
            closePrice[i] = stockDetailsContent.getClosePrice().doubleValue();
            volume[i] = stockDetailsContent.getVolume();
        }
    }

    private BigDecimal calculatePercentReturn(double[] closePrice) {
        double percentReturn = (closePrice[0] / closePrice[1] - 1) * 100;
        return new BigDecimal(percentReturn);
    }

    // should i calculate average volume like this?
    private BigDecimal calculateAverageVolume(double[] volume, int period) {
        return new BigDecimal(StatUtils.mean(Arrays.copyOfRange(volume, 0, period)));
    }

    private BigDecimal calculateVolumeRation(double[] volume, int period) {
        double averageVolume = calculateAverageVolume(volume, period).doubleValue();
        return new BigDecimal(volume[0] / averageVolume);
    }

    private BigDecimal calculateVolumeValue(double[] volume, double[] closePrice, int period) {
        double averageVolume = calculateAverageVolume(volume, period).doubleValue();
        double volumeValue = closePrice[0] * averageVolume;
        return new BigDecimal(volumeValue);
    }

    private double calculateSlopeSimpleRegression(double[] closePrice, int period) {
        double[] dataForTrend = normalizeArray(Arrays.copyOfRange(closePrice, 0, period));
        SimpleRegression simpleRegerssion = new SimpleRegression();
        int size = dataForTrend.length - 1;

        for (int i = 0; i < size; i++) {
            simpleRegerssion.addData(i, dataForTrend[i]);
        }
        return simpleRegerssion.getSlope();
    }

    private double[] normalizeArray(double[] input) {
        double[] normalized = new double[input.length];
        double max = StatUtils.max(input);
        double min = StatUtils.min(input);

        for (int i = 0; i < input.length; i++) {
            normalized[i] = normalizeData(input[i], max, min);
        }
        ArrayUtils.reverse(normalized);
        return normalized;
    }

    private double normalizeData(double number, double max, double min) {
        return (number - min) / (max - min) * 100;
    }
}
