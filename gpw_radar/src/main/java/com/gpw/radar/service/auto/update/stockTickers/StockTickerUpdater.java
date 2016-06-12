package com.gpw.radar.service.auto.update.stockTickers;

import com.gpw.radar.config.CacheConfiguration;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.repository.stock.StockDetailsRepository;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.service.parser.web.UrlStreamsGetterService;
import com.gpw.radar.service.parser.web.stock.StockBatchWebParser;
import com.gpw.radar.service.parser.web.stock.StockDetailsWebParser;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Service
public class StockTickerUpdater {

    private final Logger logger = LoggerFactory.getLogger(StockTickerUpdater.class);

    private final StockRepository stockRepository;
    private final StockDetailsRepository stockDetailsRepository;
    private final StockBatchWebParser stockBatchWebParser;
    private final StockDetailsWebParser detailsParser;
    private final UrlStreamsGetterService urlStreamsGetterService;

    @Inject
    public StockTickerUpdater(StockRepository stockRepository,
                              StockDetailsRepository stockDetailsRepository,
                              @Qualifier("gpwSiteDataParserService") StockBatchWebParser stockBatchWebParser,
                              @Qualifier("stooqDataParserService") StockDetailsWebParser detailsParser,
                              UrlStreamsGetterService urlStreamsGetterService) {
        this.stockRepository = stockRepository;
        this.stockDetailsRepository = stockDetailsRepository;
        this.stockBatchWebParser = stockBatchWebParser;
        this.detailsParser = detailsParser;
        this.urlStreamsGetterService = urlStreamsGetterService;
    }

    public Collection<String> verifyNewStockTickers(Collection<String> tickersInDb, Collection<String> allTickersInExternalSources) {
        Collection<String> tickers = new ArrayList<>(tickersInDb);
        Collection<String> allTickersExtSource = new ArrayList<>(allTickersInExternalSources);
        allTickersExtSource.removeAll(tickers);
        return allTickersExtSource;
    }

    @Scheduled(cron = "0 30 23 ? * MON-FRI")
    @Transactional
    public boolean updateStockTickers() {
        boolean updated = false;
        Set<String> tickersInDb = stockRepository.findAllTickers();
        Document document = stockBatchWebParser.getDocumentForAllStocks();
        Set<String> tickers = stockBatchWebParser.fetchAllTickers(document);

        Collection<String> newTickers = verifyNewStockTickers(tickersInDb, tickers);

        for (String ticker : newTickers) {
            Document doc = urlStreamsGetterService.getDocFromUrl("http://stooq.pl/q/?s=" + ticker);
            Stock stock = createStock(ticker, doc);
            StockDetails stockDetails = stockDetailsRepository.save(createStockDetails(stock, doc));
            updated = true;
        }
        cleanCache();
        return updated;
    }

    public Stock createStock(String ticker, Document doc) {
        String stockName = detailsParser.getStockNameFromWeb(doc);
        String stockShortName = detailsParser.getStockShortNameFromWeb(doc);
        Stock stock = new Stock();
        stock.setTicker(ticker);
        stock.setStockName(stockName);
        stock.setStockShortName(stockShortName);
        return stock;
    }

    private StockDetails createStockDetails(Stock stock, Document doc) {
        StockDetails stockDetails = new StockDetails();
        stockDetails.setStock(stock);
        stockDetails.setDate(detailsParser.parseDate(doc));
        stockDetails.setOpenPrice(detailsParser.parseOpenPrice(doc));
        stockDetails.setClosePrice(detailsParser.parseClosePrice(doc));
        stockDetails.setMinPrice(detailsParser.parseMinPrice(doc));
        stockDetails.setMaxPrice(detailsParser.parseMaxPrice(doc));
        stockDetails.setVolume(detailsParser.parseVolume(doc));
        return stockDetails;
    }

    @CacheEvict(cacheNames = {CacheConfiguration.STOCK_TICKERS_CACHE}, allEntries = true)
    private void cleanCache() {
        logger.debug("Clean cache");
    }
}
