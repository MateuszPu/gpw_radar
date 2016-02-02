package com.gpw.radar.service.database;

import com.gpw.radar.domain.database.Type;
import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.domain.stock.*;
import com.gpw.radar.repository.auto.update.FillDataStatusRepository;
import com.gpw.radar.repository.stock.*;
import com.gpw.radar.service.parser.file.stockDetails.FileStockDetailsParserService;
import com.gpw.radar.service.parser.file.stockDetails.StockDetailsParser;
import com.gpw.radar.service.parser.file.stockFiveMinutesDetails.StockFiveMinutesDetailsParser;
import com.gpw.radar.service.parser.web.stock.StockParser;
import com.gpw.radar.service.parser.web.stockFinanceEvent.StockFinanceEventParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
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

    private final Logger logger = LoggerFactory.getLogger(FillDataBaseWithDataService.class);

    @Inject
    private StockRepository stockRepository;

    @Inject
    private StockDetailsRepository stockDetailsRepository;

    @Inject
    private FillDataStatusRepository fillDataStatusRepository;

    @Inject
    private StockFinanceEventRepository stockFinanceEventRepository;

    @Inject
    private StockFiveMinutesIndicatorsRepository stockFiveMinutesIndicatorsRepository;

    @Inject
    private StockFiveMinutesDetailsRepository stockFiveMinutesDetailsRepository;

    @Inject
    private BeanFactory beanFactory;

    private int step;
    private ClassLoader classLoader = getClass().getClassLoader();
    private StockFinanceEventParser stockFinanceEventParser;
    private StockParser stockParser;
    private StockDetailsParser stockDetailsParser;
    private StockFiveMinutesDetailsParser stockFiveMinutesDetailsParser;

    @PostConstruct
    public void initParsers() {
        stockFinanceEventParser = beanFactory.getBean("stockwatchParserService", StockFinanceEventParser.class);
        stockParser = beanFactory.getBean("stooqParserService", StockParser.class);
        stockDetailsParser = beanFactory.getBean("fileStockDetailsParserService", StockDetailsParser.class);
        stockFiveMinutesDetailsParser = beanFactory.getBean("fileStockFiveMinutesDetailsParserService", StockFiveMinutesDetailsParser.class);
    }

    public ResponseEntity<Void> fillDatabaseWithData(Type type) {
        switch (type) {
            case STOCK:
                return fillDataBaseWithStocks();
            case STOCK_DETAILS:
                return fillDataBaseWithStockDetails();
            case STOCK_DETAILS_FIVE_MINUTES:
                return fillDataBaseWithStockFiveMinutesDetails();
            case STOCK_FINANCE_EVENTS:
                return fillDataBaseWithStockFinanceEvent();
            default:
                return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }
    }

    //TODO: refactor sending step as socket to application
    @Transactional
    public ResponseEntity<Void> fillDataBaseWithStocks() {
        step = 0;
        for (StockTicker element : StockTicker.values()) {
            Stock stock = new Stock();
            stock.setTicker(element);
            stock = stockParser.setNameAndShortName(stock);
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
                List<StockDetails> stockDetails = stockDetailsParser.parseStockDetails(stock, st);
                stockDetailsRepository.save(stockDetails);
                increaseStep();
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            logger.error("Error occurs: " + e.getMessage());
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
                List<StockFiveMinutesDetails> stockFiveMinutesDetails = stockFiveMinutesDetailsParser.parseStockFiveMinutesDetails(stock, inputStream);
                List<StockFiveMinutesDetails> filledStockFiveMinutesDetails = stockFiveMinutesDetailsParser.fillEmptyTimeAndCumulativeVolume(stockFiveMinutesDetails);
                List<StockFiveMinutesIndicators> fiveMinutesIndicators = stockFiveMinutesDetailsParser.calculateIndicatorsFromDetails(filledStockFiveMinutesDetails);
                stockFiveMinutesIndicatorsRepository.save(fiveMinutesIndicators);
                stockFiveMinutesDetailsRepository.save(filledStockFiveMinutesDetails);
                increaseStep();
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            logger.error("Error occurs: " + e.getMessage());
        }
        fillDataStatusRepository.updateType(Type.STOCK_DETAILS_FIVE_MINUTES.toString());
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Void> fillDataBaseWithStockFinanceEvent() {
        List<StockFinanceEvent> stockFinanceEventFromWeb = stockFinanceEventParser.getStockFinanceEventFromWeb();
        stockFinanceEventRepository.save(stockFinanceEventFromWeb);
//        fillDataStatusRepository.updateType(Type.STOCK_FINANCE_EVENTS.toString());
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    private synchronized void increaseStep() {
        step++;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
