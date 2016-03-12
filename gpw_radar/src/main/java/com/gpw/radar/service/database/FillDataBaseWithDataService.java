package com.gpw.radar.service.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.gpw.radar.service.parser.web.stock.StockDataParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.gpw.radar.domain.database.Type;
import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.domain.stock.StockFinanceEvent;
import com.gpw.radar.domain.stock.StockFiveMinutesDetails;
import com.gpw.radar.domain.stock.StockFiveMinutesIndicators;
import com.gpw.radar.repository.auto.update.FillDataStatusRepository;
import com.gpw.radar.repository.stock.StockDetailsRepository;
import com.gpw.radar.repository.stock.StockFinanceEventRepository;
import com.gpw.radar.repository.stock.StockFiveMinutesDetailsRepository;
import com.gpw.radar.repository.stock.StockFiveMinutesIndicatorsRepository;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.service.parser.file.stockDetails.StockDetailsParser;
import com.gpw.radar.service.parser.file.stockFiveMinutesDetails.StockFiveMinutesDetailsParser;
import com.gpw.radar.service.parser.web.stockFinanceEvent.StockFinanceEventParser;

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
    private StockDataParser stockDataParser;
    private StockDetailsParser stockDetailsParser;
    private StockFiveMinutesDetailsParser stockFiveMinutesDetailsParser;

    @PostConstruct
    public void initParsers() {
        stockFinanceEventParser = beanFactory.getBean("stockwatchParserService", StockFinanceEventParser.class);
        stockDataParser = beanFactory.getBean("stooqParserService", StockDataParser.class);
        stockDetailsParser = beanFactory.getBean("fileStockDetailsParserService", StockDetailsParser.class);
        stockFiveMinutesDetailsParser = beanFactory.getBean("fileStockFiveMinutesDetailsParserService", StockFiveMinutesDetailsParser.class);
    }

    //TODO: refactor sending step as socket to application
    public ResponseEntity<Void> fillDataBaseWithStocks() {
        for (StockTicker element : StockTicker.values()) {
            Document doc = null;
            try {
                doc = getDocumentFromStooqWeb(element.toString());
            } catch (IOException e) {
                logger.error("Error occurs: " + e.getMessage());
            }
            Stock stock = new Stock();
            stock.setTicker(element);
            stock.setStockName(stockDataParser.getStockNameFromWeb(doc));
            stock.setStockShortName(stockDataParser.getStockShortNameFromWeb(doc));
            stockRepository.save(stock);
        }
        fillDataStatusRepository.updateType(Type.STOCK.toString());
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

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

    public ResponseEntity<Void> fillDataBaseWithStockFinanceEvent() {
        List<StockFinanceEvent> stockFinanceEventFromWeb = stockFinanceEventParser.getStockFinanceEventFromWeb();
        stockFinanceEventRepository.save(stockFinanceEventFromWeb);
        fillDataStatusRepository.updateType(Type.STOCK_FINANCE_EVENTS.toString());
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    private Document getDocumentFromStooqWeb(String ticker) throws IOException {
        Document doc = Jsoup.connect("http://stooq.pl/q/?s=" + ticker).get();
        return doc;
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
