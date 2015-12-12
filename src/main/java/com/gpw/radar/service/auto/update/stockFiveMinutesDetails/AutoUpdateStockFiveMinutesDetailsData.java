package com.gpw.radar.service.auto.update.stockFiveMinutesDetails;

import com.gpw.radar.domain.stock.StockFiveMinutesDetails;
import com.gpw.radar.domain.stock.TimeStockFiveMinuteDetails;
import com.gpw.radar.repository.stock.StockFiveMinutesDetailsRepository;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalTime;
import java.util.List;

@Service
public class AutoUpdateStockFiveMinutesDetailsData {

    @Inject
    private StockFiveMinutesDetailsRepository stockFiveMinutesDetailsRepository;

    @Inject
    private SimpMessageSendingOperations messagingTemplate;

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

        if (!lookingTime.isBefore(LocalTime.of(9, 5)) && lookingTime.isBefore(LocalTime.of(16, 55))) {
            List<StockFiveMinutesDetails> stockFiveMinutesDetails = stockFiveMinutesDetailsProcessor.processingFiveMinutesStockDetails(lookingTime);
            sendDataToClient(stockFiveMinutesDetails, lookingTime);
            stockFiveMinutesDetailsRepository.save(stockFiveMinutesDetails);
            //ostatnia czesc to osobny cron na koniec dnia o 17.30 uzupelnic puste pola i zapisac do bazy ponownie i przeliczyc srednie
        }
    }

    public void sendDataToClient(List<StockFiveMinutesDetails> stockFiveMinutesDetails, LocalTime time) {
        TimeStockFiveMinuteDetails timeStockFiveMinuteDetails = new TimeStockFiveMinuteDetails();
        timeStockFiveMinuteDetails.setTime(time);
        timeStockFiveMinuteDetails.setListOfDetails(stockFiveMinutesDetails);
        messagingTemplate.convertAndSend("/most/active/stocks", timeStockFiveMinuteDetails);
    }
}
