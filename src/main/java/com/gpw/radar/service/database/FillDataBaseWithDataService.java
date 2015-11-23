package com.gpw.radar.service.database;

import com.gpw.radar.domain.database.Type;
import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.domain.stock.StockFiveMinutesDetails;
import com.gpw.radar.domain.stock.StockFiveMinutesIndicators;
import com.gpw.radar.repository.auto.update.FillDataStatusRepository;
import com.gpw.radar.repository.stock.StockDetailsRepository;
import com.gpw.radar.repository.stock.StockFiveMinutesDetailsRepository;
import com.gpw.radar.repository.stock.StockFiveMinutesIndicatorsRepository;
import com.gpw.radar.repository.stock.StockRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.InputStream;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class FillDataBaseWithDataService {

    @Inject
    private StockRepository stockRepository;

    @Inject
    private WebParserService webParserService;

    @Inject
    private StockDetailsRepository stockDetailsRepository;

    @Inject
    private FillDataStatusRepository fillDataStatusRepository;

    @Inject
    private StockFiveMinutesIndicatorsRepository stockFiveMinutesIndicatorsRepository;

    @Inject
    private StockFiveMinutesDetailsRepository stockFiveMinutesDetailsRepository;

    @Inject
    private StockDetailsTextFileParserService txtParser;

    private int step;

    private ClassLoader classLoader = getClass().getClassLoader();

    //TODO: refactor sending step as socket to application
    @Transactional
    public ResponseEntity<Void> fillDataBaseWithStocks() {
        step = 0;
        for (StockTicker element : StockTicker.values()) {
            Stock stock = new Stock();
            stock.setTicker(element);
            stock = webParserService.setNameAndShortName(stock);
            stockRepository.save(stock);
            increaseStep();
        }
        fillDataStatusRepository.updateType(Type.STOCK.toString());
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Void> fillDataBaseWithStockDetails() {
        step = 0;
        EnumSet<StockTicker> tickers = EnumSet.allOf(StockTicker.class);
        ExecutorService executor = Executors.newFixedThreadPool(4);

        for (StockTicker ticker : tickers) {
            executor.execute(() -> {
                Stock stock = stockRepository.findByTicker(ticker);
                String filePath = "stocks_data/daily/pl/wse_stocks/" + stock.getTicker().name() + ".txt";
                InputStream st = classLoader.getResourceAsStream(filePath);
                List<StockDetails> stockDetails = txtParser.parseStockDetailsByStockFromTxtFile(stock, st);
                stockDetailsRepository.save(stockDetails);
                increaseStep();
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        fillDataStatusRepository.updateType(Type.STOCK_DETAILS.toString());
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Void> fillDataBaseWithStockFiveMinutesDetails() {
        step = 0;
        EnumSet<StockTicker> tickers = EnumSet.allOf(StockTicker.class);
        ExecutorService executor = Executors.newFixedThreadPool(4);

        for (StockTicker ticker : tickers) {
            executor.execute(() -> {
                Stock stock = stockRepository.findByTicker(ticker);
                String filePath = "stocks_data/5min/pl/wse_stocks/" + stock.getTicker().name() + ".txt";
                InputStream inputStream = classLoader.getResourceAsStream(filePath);
                List<StockFiveMinutesDetails> stockFiveMinutesDetails = txtParser.parseStockFiveMinutesDetailsByStockFromTxtFile(stock, inputStream);
                List<StockFiveMinutesDetails> filledStockFiveMinutesDetails = txtParser.fillEmptyTimeAndCumulativeVolume(stockFiveMinutesDetails);
                List<StockFiveMinutesIndicators> fiveMinutesIndicators = calculateIndicatorsFromDetails(filledStockFiveMinutesDetails);
                stockFiveMinutesIndicatorsRepository.save(fiveMinutesIndicators);
                stockFiveMinutesDetailsRepository.save(filledStockFiveMinutesDetails);
                increaseStep();
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        fillDataStatusRepository.updateType(Type.STOCK_DETAILS.toString());
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    public List<StockFiveMinutesIndicators> calculateIndicatorsFromDetails(List<StockFiveMinutesDetails> filledStockFiveMinutesDetails) {
        List<StockFiveMinutesIndicators> fiveMinutesIndicators = new ArrayList<>();
        Stock stock = filledStockFiveMinutesDetails.get(0).getStock();
        for (LocalTime i = LocalTime.of(9, 05); i.isBefore(LocalTime.of(16, 55)); i = i.plusMinutes(5)) {
            StockFiveMinutesIndicators indicator = new StockFiveMinutesIndicators();
            LocalTime time = i;
            double average = filledStockFiveMinutesDetails.stream()
                .filter(element -> element.getTime().equals(time))
                .collect(Collectors.toList())
                .stream()
                .collect(Collectors.averagingDouble(element -> element.getCumulatedVolume()));
            indicator.setTime(time);
            indicator.setAverageVolume(average);
            indicator.setStock(stock);
            fiveMinutesIndicators.add(indicator);
        }
        return fiveMinutesIndicators;
    }

    private synchronized void increaseStep() {
        step++;
    }

    @Transactional
    public ResponseEntity<Void> fillDataBaseWithStockFinanceEvent() {
        step = 0;
        for (StockTicker element : StockTicker.values()) {
            Stock stock = stockRepository.findByTicker(element);
            webParserService.getStockFinanceEvent(stock);
            increaseStep();
        }
        fillDataStatusRepository.updateType(Type.STOCK_FINANCE_EVENTS.toString());
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
