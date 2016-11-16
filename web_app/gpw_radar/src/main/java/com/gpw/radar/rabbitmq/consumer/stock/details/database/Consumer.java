package com.gpw.radar.rabbitmq.consumer.stock.details.database;

import com.gpw.radar.config.Constants;
import com.gpw.radar.domain.rss.NewsMessage;
import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.rabbitmq.Mapper;
import com.gpw.radar.rabbitmq.consumer.stock.details.StockDetailsModel;
import com.gpw.radar.repository.stock.StockDetailsRepository;
import com.gpw.radar.repository.stock.StockRepository;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service("stockDetailsDatabaseConsumer")
@Profile("!" + Constants.SPRING_PROFILE_FAST)
public class Consumer {

    private final StockDetailsRepository stockDetailsRepository;
    private final StockRepository stockRepository;
    private final Mapper<StockDetailsModel, StockDetails> mapper;

    @Autowired
    public Consumer(StockDetailsRepository stockDetailsRepository,
                    StockRepository stockRepository,
                    Mapper mapper) {
        this.stockDetailsRepository = stockDetailsRepository;
        this.stockRepository = stockRepository;
        this.mapper = mapper;
    }

    @RabbitListener(queues = "${stock_details_updater_queue}")
    public void reciveMessage(Message message) throws InterruptedException, IOException {
        List<StockDetails> stockDetails = mapper.transformFromJsonToDomainObject(message, StockDetailsModel.class, StockDetails.class);
        LocalDate topDate = stockDetailsRepository.findTopDate();
        //set stock to each stock details
        //verify if the date of the newest part is ready to update, probably simply get the top date from DB
        // and if date from message is after it means that it is ok
    }
}
