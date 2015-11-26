package com.gpw.radar.service.auto.update.stockFiveMinutesDetails;

import com.gpw.radar.domain.stock.StockFiveMinutesDetails;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.service.parser.DateAndTimeParserService;
import com.gpw.radar.service.parser.web.CurrentStockDetailsParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

// getting data from http://stooq.pl/db
public class StooqFiveMinutesParser {

    //    http://stooq.pl/db/d/?d=20151125&t=5&u=17407230
    private final Logger logger = LoggerFactory.getLogger(StooqFiveMinutesParser.class);
    private final String cvsSplitBy = ",";

    @Inject
    private StockRepository stockRepository;

    @Inject
    private CurrentStockDetailsParserService currentStockDetailsParserService;

    @Inject
    private DateAndTimeParserService dateAndTimeParserService;

    public List<StockFiveMinutesDetails> getFiveMinutesStocksDetails(InputStreamReader inputStreamReader, LocalTime lookingTime) {
        List<StockFiveMinutesDetails> stockFiveMinutesDetailsList = new ArrayList<>();
        String line = "";
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        try {
            while ((line = bufferedReader.readLine()) != null) {
                String[] splittedLine = line.split(cvsSplitBy);
                LocalTime eventTime = dateAndTimeParserService.parseLocalTimeFromString(splittedLine[3]);

                if (lookingTime.equals(eventTime)) {
                    LocalDate localDate = dateAndTimeParserService.parseLocalDateFromString(splittedLine[2]);
                    StockFiveMinutesDetails stockFiveMinutesDetails = new StockFiveMinutesDetails();
                    stockFiveMinutesDetails.setTime(eventTime);
                    stockFiveMinutesDetails.setDate(LocalDateTime.of(localDate, eventTime));
                    stockFiveMinutesDetails.setVolume(Long.valueOf(splittedLine[8]));
                }
                //czytamy linie kazda i porownujemy czas z argumentem z metody
                //jesli sie zgadza tworzymy obiekt i dodajemy do listy
                //pozniej zastanowic sie jak uzupelnic dane takie jak cumulated volume i brakujace notowania
            }
        } catch (IOException e) {
            logger.error("Error ocurs: " + e.getMessage());
        }


        return stockFiveMinutesDetailsList;
    }

    private InputStreamReader getBufferedReader() {
        InputStreamReader inputStreamReader = currentStockDetailsParserService.getBufferedReaderFromUrl(prepareUrl());
        return inputStreamReader;
    }

    private String prepareUrl() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("http://stooq.pl/db/d/?d=");
        LocalDate today = LocalDate.now();
        stringBuffer.append(today.getYear());
        stringBuffer.append(today.getMonth());
        stringBuffer.append(today.getDayOfMonth());
        stringBuffer.append("&t=5&u=17407230");
        return stringBuffer.toString();
    }
}
