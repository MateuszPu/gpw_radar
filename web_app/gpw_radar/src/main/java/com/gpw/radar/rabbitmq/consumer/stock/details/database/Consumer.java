package com.gpw.radar.rabbitmq.consumer.stock.details.database;

import com.gpw.radar.config.CacheConfiguration;
import com.gpw.radar.config.Constants;
import com.gpw.radar.elasticsearch.stockdetails.StockDetails;
import com.gpw.radar.dao.stockdetails.StockDetailsDAO;
import com.gpw.radar.service.auto.update.stockDetails.indicators.StockIndicatorsCalculator;
import com.gpw.radar.service.mapper.JsonTransformer;
import com.gpw.radar.service.stock.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import sun.plugin.dom.exception.InvalidStateException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service("stockDetailsDatabaseConsumer")
@Profile({Constants.SPRING_PROFILE_PRODUCTION, Constants.SPRING_PROFILE_DEVELOPMENT})
public class Consumer {

    private final Logger logger = LoggerFactory.getLogger(Consumer.class);

    private final StockDetailsDAO stockDetailsDAO;
    private final StockService stockService;
    private final StockIndicatorsCalculator standardStockIndicatorsCalculator;
    private final JsonTransformer<StockDetails> jsonTransformer;

    @Autowired
    public Consumer(@Qualifier("stockDetailsElasticSearchDAO") StockDetailsDAO stockDetailsDAO,
                    StockService stockService,
                    StockIndicatorsCalculator standardStockIndicatorsCalculator,
                    JsonTransformer jsonTransformer) {
        this.stockDetailsDAO = stockDetailsDAO;
        this.stockService = stockService;
        this.standardStockIndicatorsCalculator = standardStockIndicatorsCalculator;
        this.jsonTransformer = jsonTransformer;
    }

    @RabbitListener(queues = "${stock_details_updater_queue}")
    public void consumeMessage(Message message) throws InterruptedException, IOException {
        parseStocksDetails(message);
    }

    public List<StockDetails> parseStocksDetails(Message message) throws IOException {
        List<StockDetails> stocksDetails = jsonTransformer.deserializeFromJson(message, StockDetails.class);
        LocalDate date = stocksDetails.stream()
            .findAny()
            .orElseThrow(() -> new InvalidStateException("Stock details does not have date property"))
            .getDate();
        LocalDate topDate = stockDetailsDAO.findTopDate();
        if (date.isAfter(topDate)) {
            return fillMandatoryData(stocksDetails);
        }
        logger.warn("Cannot process update as the provided stock details quoted on [{}] are currently in ES ", topDate);
        return new ArrayList<>();
    }

    private List<StockDetails> fillMandatoryData(List<StockDetails> stocksDetails) {
        stocksDetails.forEach(stockService::setNameAndShortNameOfStock);
        stockDetailsDAO.save(stocksDetails);
        standardStockIndicatorsCalculator.calculateCurrentStockIndicators();
        cleanCache();
        return stocksDetails;
    }

    @CacheEvict(cacheNames = {CacheConfiguration.STOCK_TICKERS_CACHE}, allEntries = true)
    private void cleanCache() {
        logger.debug("Clean cache");
    }
}
