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
import java.util.Optional;

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
        dbStocks.forEach(st -> stockIndicators.add(calculateStockIndicator(st)));
        return stockIndicators;
    }

    public StockIndicators calculateStockIndicator(Stock stock) {
        //TODO: refactor this, now we create over 400 select to DB
        StockIndicators stockIndicators = stockIndicatorsRepository.findByStockTicker(stock.getTicker())
            .orElse(new StockIndicators());
        Optional<IndicatorVariables> indicatorVariables1 = prepareVariables(stock);
        if (indicatorVariables1.isPresent()) {
            IndicatorVariables indicatorVariables = indicatorVariables1.get();
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
        }
        stock.setStockIndicators(stockIndicators);
        stockRepository.save(stock);
        return stockIndicators;
    }

    private Optional<IndicatorVariables> prepareVariables(Stock stock) {
        Optional<IndicatorVariables> result = Optional.empty();
        Pageable top100th = new PageRequest(0, 100);
        Page<StockDetails> stockDetails = stockDetailsRepository.findByStockOrderByDateDesc(stock, top100th);
        if (stockDetails.getContent() != null && stockDetails.getContent().size() > 2) {
            result = Optional.of(new IndicatorVariables(stockDetails.getContent()));
        }
        return result;
    }
}
