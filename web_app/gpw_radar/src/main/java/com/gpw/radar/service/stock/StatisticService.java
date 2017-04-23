package com.gpw.radar.service.stock;

import com.gpw.radar.domain.stock.StockStatistic;
import com.gpw.radar.repository.stock.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatisticService {

    private final StockRepository stockRepository;

    @Autowired
    public StatisticService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public ResponseEntity<Long> countStocksUp() {
        long countUp = stockRepository.countUpStocks();
        return new ResponseEntity<>(countUp, HttpStatus.OK);
    }

    public ResponseEntity<Long> countStocksDown() {
        long countDown = stockRepository.countDownStocks();
        return new ResponseEntity<>(countDown, HttpStatus.OK);
    }

    public ResponseEntity<Long> countStocksNoChange() {
        long countNoChange = stockRepository.countNoChangeStocks();
        return new ResponseEntity<>(countNoChange, HttpStatus.OK);
    }

    public ResponseEntity<List<StockStatistic>> getFiveMostFollowedStocks() {
        List<StockStatistic> mostFollowed = stockRepository.getTop5MostFollowedStocks()
            .stream()
            .map(this::createStockStatistic)
            .collect(Collectors.toList());
        return new ResponseEntity<>(mostFollowed, HttpStatus.OK);
    }

    private StockStatistic createStockStatistic(Object[] e) {
        double count = ((BigInteger) e[0]).doubleValue();
        String stockTicker = (String) e[1];
        return new StockStatistic(count, stockTicker);
    }
}
