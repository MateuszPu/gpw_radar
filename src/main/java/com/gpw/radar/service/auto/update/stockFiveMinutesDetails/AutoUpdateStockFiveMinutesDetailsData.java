package com.gpw.radar.service.auto.update.stockFiveMinutesDetails;

import com.gpw.radar.domain.stock.StockFiveMinutesDetails;
import com.gpw.radar.domain.stock.StockFiveMinutesIndicators;
import com.gpw.radar.domain.stock.TimeStockFiveMinuteDetails;
import com.gpw.radar.repository.stock.StockFiveMinutesDetailsRepository;
import com.gpw.radar.repository.stock.StockFiveMinutesIndicatorsRepository;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AutoUpdateStockFiveMinutesDetailsData {

    @Inject
    private StockFiveMinutesDetailsRepository stockFiveMinutesDetailsRepository;

    @Inject
    private StockFiveMinutesIndicatorsRepository stockFiveMinutesIndicatorsRepository;

    @Inject
    private SimpMessageSendingOperations messagingTemplate;

    @Inject
    private BeanFactory beanFactory;

    private StockFiveMinutesDetailsParser stockFiveMinutesDetailsParser;
    private List<StockFiveMinutesIndicators> fiveMinutesIndicators;

    @PostConstruct
    public void initParser() {
        stockFiveMinutesDetailsParser = beanFactory.getBean("stooqFiveMinutesParser", StockFiveMinutesDetailsParser.class);
    }

    @Scheduled(cron = "0 45 8 * * MON-FRI")
    public void getActualyIndicators() {
        fiveMinutesIndicators = stockFiveMinutesIndicatorsRepository.findAll();
    }

    @Transactional
    @Scheduled(cron = "30 */5 9-18 * * MON-FRI")
    public void updateStockFiveMinuteDetails() {
        LocalTime now = LocalTime.now();
        LocalTime lookingTime = LocalTime.of(now.getHour(), now.getMinute());
        lookingTime = lookingTime.minusMinutes(15);

        if (!lookingTime.isBefore(LocalTime.of(9, 5)) && lookingTime.isBefore(LocalTime.of(16, 55))) {
            List<StockFiveMinutesDetails> stockFiveMinutesDetails = stockFiveMinutesDetailsParser.parseFiveMinutesStockDetails(lookingTime);
            calculateVolumeRatio(stockFiveMinutesDetails, lookingTime);
            //ostatnia czesc to osobny cron na koniec dnia o 17.30 uzupelnic puste pola i zapisac do bazy ponownie i przeliczyc srednie
        }
    }

//    @Scheduled(cron = "0 */1 * * * *")
//    public void asd() {
//        System.out.println("WYSYLAM WIADOMOSC _______________________");
//        List<StockFiveMinutesDetails> std = stockFiveMinutesDetailsRepository.findByDate(LocalDate.of(2015, 10, 28));
//        std = std.stream().filter(st -> st.getTime().equals(LocalTime.of(16,50))).collect(Collectors.toList());
//        TimeStockFiveMinuteDetails a = new TimeStockFiveMinuteDetails();
//        a.setTime(LocalTime.of(16,50));
//        a.setListOfDetails(std);
//        messagingTemplate.convertAndSend("/most/active/stocks", a);
//    }

    private void calculateVolumeRatio(List<StockFiveMinutesDetails> stockFiveMinutesDetails, LocalTime time) {
        List<StockFiveMinutesIndicators> indicatorsToCompare = fiveMinutesIndicators.stream().filter(indicator -> indicator.getTime().equals(time)).collect(Collectors.toList());

        stockFiveMinutesDetails.stream().forEach(detail -> detail.setRatioVolume(detail.getCumulatedVolume() /
            indicatorsToCompare.stream()
                .filter(indi -> indi.getStock().equals(detail.getStock()))
                .findAny()
                .get().getAverageVolume()));

        stockFiveMinutesDetailsRepository.save(stockFiveMinutesDetails);
        TimeStockFiveMinuteDetails timeStockFiveMinuteDetails = new TimeStockFiveMinuteDetails();
        timeStockFiveMinuteDetails.setTime(time);
        timeStockFiveMinuteDetails.setListOfDetails(stockFiveMinutesDetails);
        messagingTemplate.convertAndSend("/most/active/stocks", timeStockFiveMinuteDetails);
    }
}
