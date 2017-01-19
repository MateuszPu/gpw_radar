package com.gpw.radar.service.database;

import com.gpw.radar.domain.database.Type;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockFinanceEvent;
import com.gpw.radar.domain.stock.StockFiveMinutesDetails;
import com.gpw.radar.domain.stock.StockFiveMinutesIndicators;
import com.gpw.radar.elasticsearch.domain.stockdetails.StockDetails;
import com.gpw.radar.elasticsearch.repository.StockDetailsEsRepository;
import com.gpw.radar.repository.auto.update.FillDataStatusRepository;
import com.gpw.radar.repository.stock.StockFinanceEventRepository;
import com.gpw.radar.repository.stock.StockFiveMinutesDetailsRepository;
import com.gpw.radar.repository.stock.StockFiveMinutesIndicatorsRepository;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.service.parser.DateAndTimeParserService;
import com.gpw.radar.service.parser.file.stockFiveMinutesDetails.StockFiveMinutesDetailsParser;
import com.gpw.radar.service.parser.web.UrlStreamsGetterService;
import com.gpw.radar.service.parser.web.stock.StockBatchWebParser;
import com.gpw.radar.service.parser.web.stock.StockDataDetailsWebParser;
import com.gpw.radar.service.parser.web.stockDetails.GpwSiteStockDetailsParser;
import com.gpw.radar.service.parser.web.stockDetails.StockDetailsParser;
import com.gpw.radar.service.parser.web.stockFinanceEvent.StockFinanceEventParser;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class FillDataBaseWithDataService {

    private final Logger logger = LoggerFactory.getLogger(FillDataBaseWithDataService.class);

    @Inject
    private StockRepository stockRepository;

    @Inject
    private FillDataStatusRepository fillDataStatusRepository;

    @Inject
    private StockFinanceEventRepository stockFinanceEventRepository;

    @Inject
    private StockFiveMinutesIndicatorsRepository stockFiveMinutesIndicatorsRepository;

    @Inject
    private StockFiveMinutesDetailsRepository stockFiveMinutesDetailsRepository;

    @Inject
    private UrlStreamsGetterService urlStreamsGetterService;

    @Inject
    private DateAndTimeParserService dateAndTimeParserService;

    @Inject
    private StockDetailsEsRepository esService;

    @Inject
    private BeanFactory beanFactory;

    @Value("${fill.database.stockDetails.period}")
    private int period;

    private int step;
    private ClassLoader classLoader = getClass().getClassLoader();
    private StockFinanceEventParser stockFinanceEventParser;
    private StockDataDetailsWebParser stockDataNameParser;
    private StockFiveMinutesDetailsParser stockFiveMinutesDetailsParser;
    private StockBatchWebParser stockBatchWebParser;

    @PostConstruct
    public void initParsers() {
        stockBatchWebParser = beanFactory.getBean("gpwSiteDataParserService", StockBatchWebParser.class);
        stockFinanceEventParser = beanFactory.getBean("stockwatchParserService", StockFinanceEventParser.class);
        stockDataNameParser = beanFactory.getBean("stooqDataParserService", StockDataDetailsWebParser.class);
        stockFiveMinutesDetailsParser = beanFactory.getBean("fileStockFiveMinutesDetailsParserService", StockFiveMinutesDetailsParser.class);
    }

    //TODO: refactor sending step as socket to application
    public ResponseEntity<HttpStatus> fillDataBaseWithStocks() {
        Document document = stockBatchWebParser.getDocumentForAllStocks();
        Set<String> tickers = stockBatchWebParser.fetchAllTickers(document);

        for (String element : tickers) {
            Document doc = urlStreamsGetterService.getDocFromUrl("http://stooq.pl/q/?s=" + element);
            Stock stock = new Stock();
            stock.setTicker(element);
            stock.setStockName(stockDataNameParser.getStockNameFromWeb(doc));
            String stockShortNameFromWeb = stockDataNameParser.getStockShortNameFromWeb(doc);
            if (stockShortNameFromWeb.endsWith("PDA")) {
                continue;
            }
            stock.setStockShortName(stockShortNameFromWeb);
            stockRepository.save(stock);
        }
        fillDataStatusRepository.updateType(Type.STOCK.toString());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<HttpStatus> fillDataBaseWithStockDetails() {
        List<Stock> all = stockRepository.findAll();
        StockDetailsParser stockDetailsParser = new GpwSiteStockDetailsParser(dateAndTimeParserService,
            urlStreamsGetterService, all);
        step = 0;
        LocalDate now = LocalDate.now();
        LocalDate daysAgo = now.minusDays(period);

        List<StockDetails> stockDetailsEs = new LinkedList<>();
        for (LocalDate date = daysAgo; date.isBefore(now); date = date.plusDays(1)) {
            if (date.getDayOfWeek().equals(DayOfWeek.SATURDAY) || date.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                continue;
            }
            try {
                List<StockDetails> stockDetails = stockDetailsParser.parseStockDetails(date);
                stockDetailsEs.addAll(stockDetails);
            } catch (IOException e) {
                logger.error("Error occurs: " + e.getMessage());
            }
        }
        fillDataStatusRepository.updateType(Type.STOCK_DETAILS.toString());

        esService.save(stockDetailsEs);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<HttpStatus> fillDataBaseWithStockFiveMinutesDetails() {
        step = 0;
        Set<String> tickers = stockRepository.findAllTickers();
        ExecutorService executor = Executors.newFixedThreadPool(4);

        for (String ticker : tickers) {
            executor.execute(() -> {
                Stock stock = stockRepository.findByTicker(ticker);
                String filePath = "stocks_data/5min/pl/wse_stocks/" + stock.getTicker() + ".txt";
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
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<HttpStatus> fillDataBaseWithStockFinanceEvent() {
        List<StockFinanceEvent> stockFinanceEventFromWeb = stockFinanceEventParser.getStockFinanceEventFromWeb();
        stockFinanceEventRepository.save(stockFinanceEventFromWeb);
        fillDataStatusRepository.updateType(Type.STOCK_FINANCE_EVENTS.toString());
        return new ResponseEntity<>(HttpStatus.OK);
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
