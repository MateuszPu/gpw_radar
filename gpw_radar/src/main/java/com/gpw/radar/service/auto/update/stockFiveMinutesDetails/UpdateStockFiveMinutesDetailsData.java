package com.gpw.radar.service.auto.update.stockFiveMinutesDetails;

import com.gpw.radar.config.Constants;
import com.gpw.radar.domain.stock.StockFiveMinutesDetails;
import com.gpw.radar.repository.stock.StockFiveMinutesDetailsRepository;
import com.gpw.radar.service.sockets.SocketMessageHandler;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.LocalTime;
import java.util.List;

@Service
@Profile(Constants.SPRING_PROFILE_PRODUCTION)
public class UpdateStockFiveMinutesDetailsData {

    @Inject
    private StockFiveMinutesDetailsRepository stockFiveMinutesDetailsRepository;

    @Inject
    private SocketMessageHandler socketMessageService;

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
