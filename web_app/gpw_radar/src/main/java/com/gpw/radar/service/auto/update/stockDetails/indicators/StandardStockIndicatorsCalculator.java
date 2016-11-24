package com.gpw.radar.service.auto.update.stockDetails.indicators;

import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.domain.stock.StockIndicators;
import com.gpw.radar.repository.stock.StockDetailsRepository;
import com.gpw.radar.repository.stock.StockIndicatorsRepository;
import com.gpw.radar.repository.stock.StockRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Component("standardStockIndicatorsCalculator")
public class StandardStockIndicatorsCalculator implements StockIndicatorsCalculator {

    @Inject
    private StockDetailsRepository stockDetailsRepository;

    @Inject
    private StockRepository stockRepository;

    @Inject
    private StockIndicatorsRepository stockIndicatorsRepository;

    @Override
    public List<StockIndicators> calculateCurrentStockIndicators() {
        List<StockIndicators> stockIndicators = new ArrayList<StockIndicators>();
        List<Stock> dbStocks = stockRepository.findAll();
        dbStocks.stream()
            .forEach(st -> stockIndicators.add(calculateStockIndicator(st)));
        return stockIndicatorsRepository.save(stockIndicators);
    }

    public StockIndicators calculateStockIndicator(Stock stock) {
        StockIndicators stockIndicators = stockIndicatorsRepository.findByStock(stock).orElse(new StockIndicators());
        IndicatorVariables indicatorVariables = prepareVariables(stock);

        stockIndicators.setPercentReturn(indicatorVariables.calculatePercentReturn());
        stockIndicators.setAverageVolume10Days(indicatorVariables.calculateAverageVolume(10));
        stockIndicators.setAverageVolume30Days(indicatorVariables.calculateAverageVolume(30));
        stockIndicators.setVolumeRatio10(indicatorVariables.calculateVolumeRatio(10));
        stockIndicators.setVolumeRatio30(indicatorVariables.calculateVolumeRatio(30));
        stockIndicators.setVolumeValue30Days(indicatorVariables.calculateAverageVolumeValue(30));

        stockIndicators.setSlopeSimpleRegression10Days(indicatorVariables.calculateSlopeSimpleRegression(10));
        stockIndicators.setSlopeSimpleRegression30Days(indicatorVariables.calculateSlopeSimpleRegression(30));
        stockIndicators.setSlopeSimpleRegression60Days(indicatorVariables.calculateSlopeSimpleRegression(60));
        stockIndicators.setSlopeSimpleRegression90Days(indicatorVariables.calculateSlopeSimpleRegression(90));
        if (indicatorVariables.getDate() != null) {
            stockIndicators.setDate(indicatorVariables.getDate());
        }
        stockIndicators.setStock(stock);
        return stockIndicators;
    }

    private IndicatorVariables prepareVariables(Stock stock) {
        Pageable top100th = new PageRequest(0, 100);
        Page<StockDetails> stockDetails = stockDetailsRepository.findByStockOrderByDateDesc(stock, top100th);
        return new IndicatorVariables(stockDetails.getContent());
    }
}
