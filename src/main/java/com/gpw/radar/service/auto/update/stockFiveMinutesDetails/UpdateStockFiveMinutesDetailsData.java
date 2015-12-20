package com.gpw.radar.service.auto.update.stockFiveMinutesDetails;

import com.gpw.radar.domain.stock.StockFiveMinutesDetails;
import com.gpw.radar.domain.stock.TimeStockFiveMinuteDetails;
import com.gpw.radar.repository.stock.StockFiveMinutesDetailsRepository;
import com.gpw.radar.service.parser.DateAndTimeParserService;
import com.gpw.radar.service.sockets.SocketMessageService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.LocalTime;
import java.util.List;

@Service
public class UpdateStockFiveMinutesDetailsData {

    @Inject
    private StockFiveMinutesDetailsRepository stockFiveMinutesDetailsRepository;

    @Inject
    private SocketMessageService socketMessageService;

    @Inject
    private BeanFactory beanFactory;

    private StockFiveMinutesDetailsProcessor stockFiveMinutesDetailsProcessor;

    @PostConstruct
    public void initParser() {
        stockFiveMinutesDetailsProcessor = beanFactory.getBean("stooqFiveMinutesProcessor", StockFiveMinutesDetailsProcessor.class);
    }

    @Transactional
    @Scheduled(cron = "30 */5 9-18 * * MON-FRI")
    public void updateStockFiveMinuteDetails() {
        LocalTime now = LocalTime.now();
        LocalTime lookingTime = LocalTime.of(now.getHour(), now.getMinute());
        lookingTime = lookingTime.minusMinutes(15); //we need to minus 15minutes as the downloading data is delay 15min to real quotes

        if (lookingTime.isAfter(LocalTime.of(9, 0)) && lookingTime.isBefore(LocalTime.of(16, 55))) {
            List<StockFiveMinutesDetails> stockFiveMinutesDetails = stockFiveMinutesDetailsProcessor.processDetailsByTime(lookingTime);
            socketMessageService.sendMostActiveStocksToClient(stockFiveMinutesDetails, lookingTime);
            stockFiveMinutesDetailsRepository.save(stockFiveMinutesDetails);
        }
    }
}
