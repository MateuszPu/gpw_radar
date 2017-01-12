package com.gpw.radar.rabbitmq.consumer.stock.details.database;

import com.gpw.radar.config.CacheConfiguration;
import com.gpw.radar.config.Constants;
import com.gpw.radar.elasticsearch.domain.stockdetails.StockDetails;
import com.gpw.radar.elasticsearch.service.stockdetails.StockDetailsDao;
import com.gpw.radar.rabbitmq.Mapper;
import com.gpw.radar.service.auto.update.stockDetails.indicators.StockIndicatorsCalculator;
import com.gpw.radar.service.stock.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service("stockDetailsDatabaseConsumer")
@Profile("!" + Constants.SPRING_PROFILE_DEVELOPMENT)
public class Consumer {

    private final Logger logger = LoggerFactory.getLogger(Consumer.class);

    private final StockDetailsDao stockDetailsDaoEs;
    private final StockService stockService;
    private final StockIndicatorsCalculator standardStockIndicatorsCalculator;
    private final Mapper<StockDetails, StockDetails> mapper;

    @Autowired
    public Consumer(StockDetailsDao stockDetailsDaoEs,
                    StockService stockService,
                    StockIndicatorsCalculator standardStockIndicatorsCalculator,
                    Mapper mapper) {
        this.stockDetailsDaoEs = stockDetailsDaoEs;
        this.stockService = stockService;
        this.standardStockIndicatorsCalculator = standardStockIndicatorsCalculator;
        this.mapper = mapper;
    }

    @RabbitListener(queues = "${stock_details_updater_queue}")
    public void consumeMessage(Message message) throws InterruptedException, IOException {
        parseStocksDetails(message);
    }

    public List<StockDetails> parseStocksDetails(Message message) throws IOException {
        List<StockDetails> stocksDetails = mapper.deserializeFromJson(message, StockDetails.class);
        LocalDate date = stocksDetails.stream().findAny().get().getDate();
        LocalDate topDate = stockDetailsDaoEs.findTopDate();
        if (date.isAfter(topDate)) {
            return fillMandatoryData(stocksDetails);
        }
        logger.warn("Cannot process update as the provided stock details quoted on [{}] are currently in ES ", topDate);
        return new ArrayList<>();
    }

    private List<StockDetails> fillMandatoryData(List<StockDetails> stocksDetails) {
        stocksDetails.forEach(e -> stockService.addMissingData(e));
        stockDetailsDaoEs.save(stocksDetails);
        standardStockIndicatorsCalculator.calculateCurrentStockIndicators();
        cleanCache();
        return stocksDetails;
    }

    @CacheEvict(cacheNames = {CacheConfiguration.STOCK_TICKERS_CACHE}, allEntries = true)
    private void cleanCache() {
        logger.debug("Clean cache");
    }
}
