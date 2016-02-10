package com.gpw.radar.service.auto.update.stockFiveMinutesDetails;

import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockFiveMinutesDetails;
import com.gpw.radar.domain.stock.StockFiveMinutesIndicators;
import com.gpw.radar.repository.stock.StockFiveMinutesIndicatorsRepository;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.service.parser.DateAndTimeParserService;
import com.gpw.radar.service.parser.web.CurrentStockDetailsParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// getting data from http://stooq.pl/db
@Component("stooqFiveMinutesProcessor")
public class StooqFiveMinutesProcessor implements StockFiveMinutesDetailsProcessor {

    //    http://stooq.pl/db/d/?d=20151125&t=5&u=17407230
    private final Logger logger = LoggerFactory.getLogger(StooqFiveMinutesProcessor.class);

    @Inject
    private StockRepository stockRepository;

    @Inject
    private StockFiveMinutesIndicatorsRepository stockFiveMinutesIndicatorsRepository;

    @Inject
    private CurrentStockDetailsParserService currentStockDetailsParserService;

    @Inject
    private DateAndTimeParserService dateAndTimeParserService;

    private List<Stock> stocksInApp;
    private List<StockFiveMinutesDetails> lastStockFiveMinuteDetails = new ArrayList<>();;
    private List<StockFiveMinutesIndicators> fiveMinutesIndicators;

    public List<StockFiveMinutesDetails> processDetailsByTime(LocalTime lookingTime) {
        List<StockFiveMinutesDetails> processedDetails = new ArrayList<StockFiveMinutesDetails>();
        processedDetails = getCurrentFiveMinutesStockDetails(getInputStreamReader(), lookingTime);
        processedDetails = fillEmptyTimeWithData(processedDetails, lastStockFiveMinuteDetails);
        processedDetails = calculateCumulatedVolume(processedDetails, lookingTime);
        processedDetails = setStockToEachDetail(processedDetails);
        processedDetails = calculateVolumeRatio(processedDetails, lookingTime);
        return processedDetails;
    }

    public List<StockFiveMinutesDetails> getCurrentFiveMinutesStockDetails(InputStreamReader inputStreamReader, LocalTime lookingTime) {
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        List<StockFiveMinutesDetails> stockFiveMinutesDetailsList = bufferedReader.lines()
            .map(line -> mapToStockFiveMinutesDetails(line))
            .filter(stFvDt -> stFvDt.isPresent())
            .filter(stockFiveMinutesDetails -> stockFiveMinutesDetails.get().getTime().equals(lookingTime))
            .map(st -> st.get())
            .collect(Collectors.toList());

        try {
            bufferedReader.close();
            inputStreamReader.close();
        } catch (IOException e) {
            logger.error("Error occurs: " + e.getMessage());
        }

        return stockFiveMinutesDetailsList;
    }

    //TODO: consider move query to database to postconstruct method, or maybe better while application is reading
    // stock 5 minutes details line by line there should be check if the application exists in app
    private Optional<StockFiveMinutesDetails> mapToStockFiveMinutesDetails(String line) {
        String[] splitLine = line.split(",");
        StockFiveMinutesDetails stockFiveMinutesDetails = new StockFiveMinutesDetails();

        try {
            StockTicker ticker = StockTicker.valueOf(splitLine[0].toLowerCase());
            stockFiveMinutesDetails.setStockTicker(ticker);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }

        LocalTime eventTime = dateAndTimeParserService.parseLocalTimeFromString(splitLine[3]);
        LocalDate eventDate = dateAndTimeParserService.parseLocalDateFromString(splitLine[2]);
        stockFiveMinutesDetails.setTime(eventTime);
        stockFiveMinutesDetails.setDate(eventDate);
        stockFiveMinutesDetails.setVolume(Long.valueOf(splitLine[8]));

        return Optional.of(stockFiveMinutesDetails);
    }

    public List<StockFiveMinutesDetails> fillEmptyTimeWithData(List<StockFiveMinutesDetails> detailsList, List<StockFiveMinutesDetails> lastStockFiveMinuteDetails) {
        List<StockFiveMinutesDetails> filledList = new ArrayList<>(detailsList);

        if (lastStockFiveMinuteDetails.isEmpty()) {
            return detailsList;
        }
        lastStockFiveMinuteDetails.forEach(e -> e.setId(null));
        List<StockTicker> activeTickersDuringFiveMinuteSession = detailsList.stream().map(e -> e.getStockTicker()).collect(Collectors.toList());
        List<StockFiveMinutesDetails> a = lastStockFiveMinuteDetails.stream().filter(e -> !activeTickersDuringFiveMinuteSession.contains(e.getStockTicker())).collect(Collectors.toList());
        a.forEach(el -> el.setVolume(0));
        a.forEach(el -> el.setTime(el.getTime().plusMinutes(5)));

        filledList.addAll(a);

        return filledList;
    }

    public List<StockFiveMinutesDetails> calculateCumulatedVolume(List<StockFiveMinutesDetails> stockFiveMinutesDetails, LocalTime timeOfDetails) {
        List<StockFiveMinutesDetails> stockFiveMinutesDetailsToCalculateCumulatedVolume = new ArrayList<>(stockFiveMinutesDetails);

        if (!timeOfDetails.isAfter(LocalTime.of(9, 5))) {
            stockFiveMinutesDetailsToCalculateCumulatedVolume.forEach(st -> st.setCumulatedVolume(st.getVolume()));
        } else {
            stockFiveMinutesDetailsToCalculateCumulatedVolume.forEach(st -> st.setCumulatedVolume(sumVolume(st)));
        }

        lastStockFiveMinuteDetails = new ArrayList<>(stockFiveMinutesDetailsToCalculateCumulatedVolume);
        return stockFiveMinutesDetailsToCalculateCumulatedVolume;
    }

    private long sumVolume(StockFiveMinutesDetails st) {
        StockTicker stockTicker = st.getStockTicker();
        Optional<StockFiveMinutesDetails> stockFiveMinutesDetails = lastStockFiveMinuteDetails.stream()
            .filter(stFvDe -> stFvDe.getStockTicker().equals(stockTicker)).findFirst();

        if (stockFiveMinutesDetails.isPresent()) {
            return st.getVolume() + stockFiveMinutesDetails.get().getCumulatedVolume();
        }
        return st.getVolume();
    }

    public List<StockFiveMinutesDetails> setStockToEachDetail(List<StockFiveMinutesDetails> parsedList) {
        stocksInApp = stockRepository.findAll();
        parsedList.stream().forEach(stFvDt -> stFvDt.setStock(stocksInApp.stream()
            .filter(stc -> stc.getTicker().equals(stFvDt.getStockTicker()))
            .findAny()
            .get()));
        return parsedList;
    }

    public List<StockFiveMinutesDetails> calculateVolumeRatio(List<StockFiveMinutesDetails> stockFiveMinutesDetails, LocalTime time) {
        fiveMinutesIndicators = stockFiveMinutesIndicatorsRepository.findAll();
        List<StockFiveMinutesIndicators> indicatorsToCompare = fiveMinutesIndicators.stream().filter(indicator -> indicator.getTime().equals(time)).collect(Collectors.toList());

        stockFiveMinutesDetails.stream().forEach(detail -> detail.setRatioVolume(detail.getCumulatedVolume() /
            indicatorsToCompare.stream()
                .filter(indi -> indi.getStock().getTicker().equals(detail.getStockTicker()))
                .findAny()
                .get().getAverageVolume()));

        return stockFiveMinutesDetails;
    }

    public InputStreamReader getInputStreamReader() {
        InputStreamReader inputStreamReader = currentStockDetailsParserService.getInputStreamReaderFromUrl(prepareUrl());
        return inputStreamReader;
    }

    private String prepareUrl() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("http://stooq.pl/db/d/?d=");
        LocalDate today = LocalDate.now();
        stringBuffer.append(today.getYear());
        String formattedMonth = String.format("%02d", today.getMonthValue());
        stringBuffer.append(formattedMonth);
        String formattedDay = String.format("%02d", today.getDayOfMonth());
        stringBuffer.append(formattedDay);
        stringBuffer.append("&t=5&u=17545139");
        return stringBuffer.toString();
    }

    public void setLastStockFiveMinuteDetails(List<StockFiveMinutesDetails> lastStockFiveMinuteDetails) {
        this.lastStockFiveMinuteDetails = lastStockFiveMinuteDetails;
    }
}
