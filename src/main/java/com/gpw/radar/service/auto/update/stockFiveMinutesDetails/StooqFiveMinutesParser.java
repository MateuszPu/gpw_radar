package com.gpw.radar.service.auto.update.stockFiveMinutesDetails;

import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockFiveMinutesDetails;
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

// getting data from http://stooq.pl/db
@Component("stooqFiveMinutesParser")
public class StooqFiveMinutesParser implements StockFiveMinutesDetailsParser {

    //    http://stooq.pl/db/d/?d=20151125&t=5&u=17407230
    private final Logger logger = LoggerFactory.getLogger(StooqFiveMinutesParser.class);

    @Inject
    private StockRepository stockRepository;

    @Inject
    private CurrentStockDetailsParserService currentStockDetailsParserService;

    @Inject
    private DateAndTimeParserService dateAndTimeParserService;

    private List<Stock> stocksInApp;
    private List<StockFiveMinutesDetails> lastStockFiveMinuteDetails;

    public List<StockFiveMinutesDetails> parseFiveMinutesStockDetails(LocalTime lookingTime) {
        List<StockFiveMinutesDetails> parsedList = new ArrayList<StockFiveMinutesDetails>();
        parsedList = getCurrentFiveMinutesStockDetails(getInputStreamReader(), lookingTime);
        parsedList = calculateCumulatedVolume(parsedList);
        parsedList = filterDetailsOfStockInApp(parsedList);
        return parsedList;
    }


    public List<StockFiveMinutesDetails> getCurrentFiveMinutesStockDetails(InputStreamReader inputStreamReader, LocalTime lookingTime) {
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        List<StockFiveMinutesDetails> stockFiveMinutesDetailsList = bufferedReader.lines()
            .map(line -> mapToStockFiveMinutesDetails(line))
            .filter(stockFiveMinutesDetails -> stockFiveMinutesDetails.getTime().equals(lookingTime))
            .collect(Collectors.toList());

        try {
            bufferedReader.close();
        } catch (IOException e) {
            logger.error("Error occurs: " + e.getMessage());
        }

        return stockFiveMinutesDetailsList;
    }

    //TODO: consider move query to database to postconstruct method, or maybe better while application is reading
    // stock 5 minutes details line by line there should be check if the application exists in app
    private StockFiveMinutesDetails mapToStockFiveMinutesDetails(String line) {
        String[] splitLine = line.split(",");
        StockFiveMinutesDetails stockFiveMinutesDetails = new StockFiveMinutesDetails();
        stockFiveMinutesDetails.setStockTicker(splitLine[0].toLowerCase());
        LocalTime eventTime = dateAndTimeParserService.parseLocalTimeFromString(splitLine[3]);
        LocalDate eventDate = dateAndTimeParserService.parseLocalDateFromString(splitLine[2]);
        stockFiveMinutesDetails.setTime(eventTime);
        stockFiveMinutesDetails.setDate(eventDate);
        stockFiveMinutesDetails.setVolume(Long.valueOf(splitLine[8]));

        return stockFiveMinutesDetails;
    }

    public List<StockFiveMinutesDetails> calculateCumulatedVolume(List<StockFiveMinutesDetails> stockFiveMinutesDetails) {
        List<StockFiveMinutesDetails> stockFiveMinutesDetailsToCalculateCumulatedVolume = stockFiveMinutesDetails;
        LocalTime timeOfDetails = stockFiveMinutesDetails.get(0).getTime();

        if (!timeOfDetails.isAfter(LocalTime.of(9, 5))) {
            stockFiveMinutesDetailsToCalculateCumulatedVolume.forEach(st -> st.setCumulatedVolume(st.getVolume()));
        } else {
            stockFiveMinutesDetailsToCalculateCumulatedVolume.forEach(st -> st.setCumulatedVolume(sumVolume(st)));
        }

        lastStockFiveMinuteDetails = stockFiveMinutesDetailsToCalculateCumulatedVolume;
        return stockFiveMinutesDetailsToCalculateCumulatedVolume;
    }

    private long sumVolume(StockFiveMinutesDetails st) {
        String stockTicker = st.getStockTicker();
        Optional<StockFiveMinutesDetails> stockFiveMinutesDetails = lastStockFiveMinuteDetails.stream()
            .filter(stFvDe -> stFvDe.getStockTicker().equals(stockTicker)).findFirst();

        if (stockFiveMinutesDetails.isPresent()) {
            return st.getVolume() + stockFiveMinutesDetails.get().getCumulatedVolume();
        }
        return st.getVolume();
    }

    public List<StockFiveMinutesDetails> filterDetailsOfStockInApp(List<StockFiveMinutesDetails> stockFiveMinutesDetails) {
        stocksInApp = stockRepository.findAll();
        stockFiveMinutesDetails.stream().forEach(stFvDt -> stFvDt.setStock(getStockByString(stFvDt.getStockTicker())));
        List<StockFiveMinutesDetails> filteredDetails = stockFiveMinutesDetails.stream()
            .filter(filtered -> filtered.getStock().getId() != null)
            .collect(Collectors.toList());
        return filteredDetails;
    }

    private Stock getStockByString(String stockTicker) {
        Stock stock = new Stock();
        try {
            StockTicker ticker = StockTicker.valueOf(stockTicker);
            stock = stocksInApp.stream().filter(stc -> stc.getTicker().equals(ticker)).findAny().get();
        } catch (IllegalArgumentException e) {
            return stock; //return stock without id which will be possible removed details without stock
        }
        return stock;
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
}
