package com.gpw.radar.service.auto.update.stockFiveMinutesDetails;

import com.gpw.radar.domain.stock.StockFiveMinutesDetails;
import com.gpw.radar.domain.stock.StockFiveMinutesIndicators;
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

        if (!lookingTime.isBefore(LocalTime.of(9, 20)) && lookingTime.isBefore(LocalTime.of(17, 10))) {
            List<StockFiveMinutesDetails> stockFiveMinutesDetails = stockFiveMinutesDetailsParser.parseFiveMinutesStockDetails(lookingTime);
            compareToIndicators(stockFiveMinutesDetails, lookingTime);
            //ostatnia czesc to osobny cron na koniec dnia o 17.30 uzupelnic puste pola i zapisac do bazy ponownie i przeliczyc srednie
        }
    }

//    @Scheduled(cron="*/45 * * * * ?")
//    public void dobraMozeInnaNazwaTejJebanejMetody() {
//        System.out.println("WYSYLAM WIADOMOSC _______________________");
//        List<StockFiveMinutesDetails> std = stockFiveMinutesDetailsRepository.findAll();
//        messagingTemplate.convertAndSend("/most/active/stocks", std.subList(0, 25));
//    }

    private void compareToIndicators(List<StockFiveMinutesDetails> stockFiveMinutesDetails, LocalTime time) {
        List<StockFiveMinutesIndicators> indicators = fiveMinutesIndicators.stream().filter(indicator -> indicator.getTime().equals(time)).collect(Collectors.toList());

        stockFiveMinutesDetails.stream().forEach(detail -> detail.setRatioVolume(detail.getCumulatedVolume() /
            indicators.stream()
                .filter(indi -> indi.getStock().equals(detail.getStock()))
                .findAny()
                .get().getAverageVolume()));

        stockFiveMinutesDetailsRepository.save(stockFiveMinutesDetails);

        List<StockFiveMinutesDetails> sprtedByVolumeRation = stockFiveMinutesDetails.stream()
            .sorted((stFvDt1, stFvDt2) -> Double.compare(stFvDt1.getRatioVolume(), stFvDt2.getCumulatedVolume()))
            .collect(Collectors.toList());

        messagingTemplate.convertAndSend("/most/active/stocks", sprtedByVolumeRation);
    }
}
