package com.gpw.radar.service.auto.update.stockFiveMinutesDetails.indicators;

import com.gpw.radar.domain.stock.StockFiveMinutesDetails;
import com.gpw.radar.domain.stock.StockFiveMinutesIndicators;
import com.gpw.radar.repository.stock.StockFiveMinutesDetailsRepository;
import com.gpw.radar.repository.stock.StockFiveMinutesIndicatorsRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StandardStockFiveMinutesDetailsCalculator {

    @Inject
    private StockFiveMinutesIndicatorsRepository stockFiveMinutesIndicatorsRepository;

    @Inject
    private StockFiveMinutesDetailsRepository stockFiveMinutesDetailsRepository;

    private List<StockFiveMinutesDetails> stockFiveMinutesDetails;

    @Scheduled(cron = "0 0 04 ? * MON-FRI")
    public void updateIndicators() {
        stockFiveMinutesDetails = stockFiveMinutesDetailsRepository.findAll();
        List<StockFiveMinutesIndicators> indicators = stockFiveMinutesIndicatorsRepository.findAll();
        indicators.stream().parallel().forEach(indicator -> indicator.setAverageVolume(calculateAverageVolume(indicator)));
        stockFiveMinutesIndicatorsRepository.save(indicators);
    }

    private double calculateAverageVolume(StockFiveMinutesIndicators indicator) {
        List<StockFiveMinutesDetails> fiveMinutesDetails = stockFiveMinutesDetails.stream()
            .filter(element -> element.getStock().getTicker().equals(indicator.getStock().getTicker()))
            .filter(element -> element.getTime().equals(indicator.getTime()))
            .sorted((e2, e1) -> e1.getDate().compareTo(e2.getDate()))
            .limit(20)
            .collect(Collectors.toList());

        return fiveMinutesDetails.stream().mapToDouble(details -> details.getCumulatedVolume()).average().getAsDouble();
    }
}
