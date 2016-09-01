package com.gpw.radar.service.stock;

import com.gpw.radar.domain.rss.NewsMessage;
import com.gpw.radar.domain.stock.StockStatistic;
import com.gpw.radar.repository.rss.NewsMessageRepository;
import com.gpw.radar.repository.stock.StockRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StatisticService {

    @Inject
    private StockRepository stockRepository;

    @Inject
    private NewsMessageRepository newsMessageRepository;

    public ResponseEntity<Long> countStocksUp() {
        long countUp = stockRepository.countUpStocks();
        return new ResponseEntity<Long>(countUp, HttpStatus.OK);
    }

    public ResponseEntity<Long> countStocksDown() {
        long countDown = stockRepository.countDownStocks();
        return new ResponseEntity<Long>(countDown, HttpStatus.OK);
    }

    public ResponseEntity<Long> countStocksNoChange() {
        long countNoChange = stockRepository.countNoChangeStocks();
        return new ResponseEntity<Long>(countNoChange, HttpStatus.OK);
    }

    public ResponseEntity<List<StockStatistic>> getFiveMostFollowedStocks() {
        List<StockStatistic> mostFollowed = stockRepository.getTop5MostFollowedStocks().stream().map(e -> new StockStatistic(((BigInteger) e[0]).doubleValue(), (String) e[1])).collect(Collectors.toList());
        return new ResponseEntity<List<StockStatistic>>(mostFollowed, HttpStatus.OK);
    }

    public ResponseEntity<Set<NewsMessage>> getFiveLatestNewsMessage() {
        return new ResponseEntity<Set<NewsMessage>>(newsMessageRepository.findTop5ByOrderByNewsDateTimeDesc(), HttpStatus.OK);
    }
}
