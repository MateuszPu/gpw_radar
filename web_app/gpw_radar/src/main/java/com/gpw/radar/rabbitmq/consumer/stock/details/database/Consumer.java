package com.gpw.radar.rabbitmq.consumer.stock.details.database;

import com.gpw.radar.config.CacheConfiguration;
import com.gpw.radar.config.Constants;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.rabbitmq.Mapper;
import com.gpw.radar.rabbitmq.consumer.stock.details.StockDetailsModel;
import com.gpw.radar.repository.stock.StockDetailsRepository;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.service.parser.web.UrlStreamsGetterService;
import com.gpw.radar.service.parser.web.stock.StockDataDetailsWebParser;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service("stockDetailsDatabaseConsumer")
@Profile("!" + Constants.SPRING_PROFILE_FAST)
public class Consumer {

    private final Logger logger = LoggerFactory.getLogger(Consumer.class);

    private final StockDetailsRepository stockDetailsRepository;
    private final StockRepository stockRepository;
    private final StockDataDetailsWebParser detailsParser;
    private final UrlStreamsGetterService urlStreamsGetterService;
    private final Mapper<StockDetailsModel, StockDetails> mapper;

    @Autowired
    public Consumer(StockDetailsRepository stockDetailsRepository,
                    StockRepository stockRepository,
                    @Qualifier("stooqDataParserService") StockDataDetailsWebParser detailsParser,
                    UrlStreamsGetterService urlStreamsGetterService,
                    Mapper mapper) {
        this.stockDetailsRepository = stockDetailsRepository;
        this.stockRepository = stockRepository;
        this.detailsParser = detailsParser;
        this.urlStreamsGetterService = urlStreamsGetterService;
        this.mapper = mapper;
    }

    @RabbitListener(queues = "${stock_details_updater_queue}")
    public void consumeMessage(Message message) throws InterruptedException, IOException {
        processMessage(message);
    }

    public List<StockDetails> processMessage(Message message) throws IOException {
        List<StockDetails> stocksDetails = mapper.transformFromJsonToDomainObject(message, StockDetailsModel.class, StockDetails.class);
        LocalDate date = stocksDetails.stream().findAny().get().getDate();
        return processUpdate(stocksDetails, date);
    }

    @Transactional
    private List<StockDetails> processUpdate(List<StockDetails> stocksDetails, LocalDate date) {
        LocalDate topDate = stockDetailsRepository.findTopDate();
        List<Stock> stocks = stockRepository.findAll();

        if (date.isAfter(topDate)) {
            stocksDetails.forEach(e -> e.setStock(getCorrectStock(getTicker(e), stocks)));
            stockDetailsRepository.save(stocksDetails);
        } else {
            logger.warn("Cannot update stock details as the date is invalid " + topDate);
        }

        cleanCache();
        //calculate stock indicators
        return stocksDetails;
    }

    private String getTicker(StockDetails stockDetails) {
        return stockDetails.getStock().getTicker().toLowerCase();
    }

    private Stock getCorrectStock(String ticker, List<Stock> stocks) {
        Optional<Stock> stock = stocks.stream().filter(e -> e.getTicker().equalsIgnoreCase(ticker)).findAny();
        if (!stock.isPresent()) {
            Document doc = urlStreamsGetterService.getDocFromUrl("http://stooq.pl/q/?s=" + ticker);
            stock = Optional.of(createStock(ticker, doc));
        }
        return stock.get();
    }

    private Stock createStock(String ticker, Document doc) {
        System.out.println("create stock with ticker " + ticker);
        Stock stock = new Stock();
        stock.setTicker(ticker);
        String stockName = detailsParser.getStockNameFromWeb(doc);
        String stockShortName = detailsParser.getStockShortNameFromWeb(doc);
        stock.setStockName(stockName);
        stock.setStockShortName(stockShortName);
        return stockRepository.save(stock);
    }

    @CacheEvict(cacheNames = {CacheConfiguration.STOCK_TICKERS_CACHE}, allEntries = true)
    private void cleanCache() {
        logger.debug("Clean cache");
    }
}
