package com.gpw.radar.service.auto.update.stockDetails.indicators;

import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockIndicators;
import com.gpw.radar.elasticsearch.domain.stockdetails.StockDetails;
import com.gpw.radar.elasticsearch.service.stockdetails.StockDetailsDAO;
import com.gpw.radar.repository.stock.StockIndicatorsRepository;
import com.gpw.radar.repository.stock.StockRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component("standardStockIndicatorsCalculator")
public class StandardStockIndicatorsCalculator implements StockIndicatorsCalculator {

    private final StockDetailsDAO stockDetailsDAO;
    private final StockRepository stockRepository;
    private final StockIndicatorsRepository stockIndicatorsRepository;

    public StandardStockIndicatorsCalculator(@Qualifier("stockDetailsElasticSearchDAO") StockDetailsDAO stockDetailsDAO, StockRepository stockRepository,
                                             StockIndicatorsRepository stockIndicatorsRepository) {
        this.stockDetailsDAO = stockDetailsDAO;
        this.stockRepository = stockRepository;
        this.stockIndicatorsRepository = stockIndicatorsRepository;
    }

    @Override
    public List<StockIndicators> calculateCurrentStockIndicators() {
        List<StockIndicators> stockIndicators = new ArrayList<StockIndicators>();
        List<Stock> dbStocks = stockRepository.findAll();
        dbStocks.forEach(st -> stockIndicators.add(calculateStockIndicator(st)));
        return stockIndicators;
    }

    public StockIndicators calculateStockIndicator(Stock stock) {
        //TODO: refactor this, now we create over 400 select to DB
        StockIndicators stockIndicators = stockIndicatorsRepository.findByStockTicker(stock.getTicker())
            .orElse(new StockIndicators());
        Optional<IndicatorCalculator> indicatorVariables1 = prepareVariables(stock);
        if (indicatorVariables1.isPresent()) {
            IndicatorCalculator indicatorCalculator = indicatorVariables1.get();
            stockIndicators.setPercentReturn(indicatorCalculator.calculatePercentReturn());
            stockIndicators.setAverageVolume10Days(indicatorCalculator.calculateAverageVolume(10));
            stockIndicators.setAverageVolume30Days(indicatorCalculator.calculateAverageVolume(30));
            stockIndicators.setVolumeRatio10(indicatorCalculator.calculateVolumeRatio(10));
            stockIndicators.setVolumeRatio30(indicatorCalculator.calculateVolumeRatio(30));
            stockIndicators.setVolumeValue30Days(indicatorCalculator.calculateAverageVolumeValue(30));

            stockIndicators.setSlopeSimpleRegression10Days(indicatorCalculator.calculateSlopeSimpleRegression(10));
            stockIndicators.setSlopeSimpleRegression30Days(indicatorCalculator.calculateSlopeSimpleRegression(30));
            stockIndicators.setSlopeSimpleRegression60Days(indicatorCalculator.calculateSlopeSimpleRegression(60));
            stockIndicators.setSlopeSimpleRegression90Days(indicatorCalculator.calculateSlopeSimpleRegression(90));
            if (indicatorCalculator.getDate() != null) {
                stockIndicators.setDate(indicatorCalculator.getDate());
            }
        }
        stock.setStockIndicators(stockIndicators);
        stockRepository.save(stock);
        return stockIndicators;
    }

    private Optional<IndicatorCalculator> prepareVariables(Stock stock) {
        Optional<IndicatorCalculator> result = Optional.empty();
        Pageable top100th = new PageRequest(0, 100);
        List<StockDetails> stockDetails = stockDetailsDAO.findByStockTickerOrderByDateDesc(stock.getTicker(), top100th);
        if (stockDetails != null && stockDetails.size() > 2) {
            result = Optional.of(new IndicatorCalculator(stockDetails));
        }
        return result;
    }
}
