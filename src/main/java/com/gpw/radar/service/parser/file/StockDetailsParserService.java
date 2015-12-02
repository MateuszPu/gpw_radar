package com.gpw.radar.service.parser.file;

import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.domain.stock.StockFiveMinutesDetails;
import com.gpw.radar.service.parser.DateAndTimeParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StockDetailsParserService {

    private final Logger logger = LoggerFactory.getLogger(StockDetailsParserService.class);
    private final String cvsSplitBy = ",";

    @Inject
    private DateAndTimeParserService dateAndTimeParserService;

    public List<StockDetails> parseStockDetails(Stock stock, InputStream st) {
        List<StockDetails> stockDetailsList = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(st));
        stockDetailsList = bufferedReader.lines().map(mapToStockDetails).collect(Collectors.toList());
        stockDetailsList.forEach(stockDetails -> stockDetails.setStock(stock));
        try {
            bufferedReader.close();
        } catch (IOException e) {
            logger.error("Error occurs: " + e.getMessage());
        }

        return stockDetailsList;
    }

    public Function<String, StockDetails> mapToStockDetails = (line) -> {
        String[] splitLine = line.split(",");
        StockDetails stockDetails = new StockDetails();
        stockDetails.setDate(dateAndTimeParserService.parseLocalDateFromString(splitLine[0]));
        stockDetails.setOpenPrice(new BigDecimal(splitLine[1]));
        stockDetails.setMaxPrice(new BigDecimal(splitLine[2]));
        stockDetails.setMinPrice(new BigDecimal(splitLine[3]));
        stockDetails.setClosePrice(new BigDecimal(splitLine[4]));

        try {
            stockDetails.setVolume(Long.valueOf(splitLine[5]));
        } catch (ArrayIndexOutOfBoundsException exc) {
            stockDetails.setVolume(0l);
        }
        return stockDetails;
    };

    public List<StockFiveMinutesDetails> parseStockFiveMinutesDetails(Stock stock, InputStream st) {
        List<StockFiveMinutesDetails> stockFiveMinutesDetailsList = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(st));
        stockFiveMinutesDetailsList = bufferedReader.lines().map(mapToStockFiveMinutesDetails).collect(Collectors.toList());
        stockFiveMinutesDetailsList.forEach(stockFiveMinutesDetails -> stockFiveMinutesDetails.setStock(stock));

        try {
            bufferedReader.close();
        } catch (IOException e) {
            logger.error("Error occurs: " + e.getMessage());
        }
        return stockFiveMinutesDetailsList;
    }

    public Function<String, StockFiveMinutesDetails> mapToStockFiveMinutesDetails = (line) -> {
        String[] splitLine = line.split(",");
        StockFiveMinutesDetails stockFiveMinutesDetails = new StockFiveMinutesDetails();
        LocalDate dateDetail = dateAndTimeParserService.parseLocalDateFromString(splitLine[0]);
        LocalTime timeDetail = dateAndTimeParserService.parseLocalTimeFromString(splitLine[1]);
        stockFiveMinutesDetails.setDate(dateDetail);
        stockFiveMinutesDetails.setTime(timeDetail);
        stockFiveMinutesDetails.setCumulatedVolume(0l);

        try {
            stockFiveMinutesDetails.setVolume(Long.valueOf(splitLine[6]));
        } catch (ArrayIndexOutOfBoundsException exc) {
            stockFiveMinutesDetails.setVolume(0l);
        }
        return stockFiveMinutesDetails;
    };

    //TODO: refactor this method
    public List<StockFiveMinutesDetails> fillEmptyTimeAndCumulativeVolume(List<StockFiveMinutesDetails> stockFiveMinutesDetailsList) {
        List<StockFiveMinutesDetails> filledEmptyTimeAndCumulativeVolume = new ArrayList<>();

        LocalDate startDate = stockFiveMinutesDetailsList.get(0).getDate().minusDays(1);

        int i = 0;
        for (StockFiveMinutesDetails element : stockFiveMinutesDetailsList) {
            if (!element.getDate().isEqual(startDate)) {
                startDate = element.getDate();
                element.setCumulatedVolume(element.getVolume());
                filledEmptyTimeAndCumulativeVolume.add(element);
                i++;
                continue;
            }

            int previousElementToCompare = i - 1;
            LocalTime timeOfEvent = element.getTime();
            LocalTime timeOfPreviousEvent = stockFiveMinutesDetailsList.get(previousElementToCompare).getTime();
            int differenceInSeconds = timeOfEvent.toSecondOfDay() - timeOfPreviousEvent.toSecondOfDay();
            int differenceInMinutes = differenceInSeconds / 60;
            if (differenceInMinutes > 5) {
                int lengthOfDifference = differenceInMinutes / 5;
                for (int j = 1; j < lengthOfDifference; j++) {
                    StockFiveMinutesDetails emptyFiveMinutesDetails = new StockFiveMinutesDetails();
                    emptyFiveMinutesDetails.setDate(stockFiveMinutesDetailsList.get(previousElementToCompare).getDate());
                    emptyFiveMinutesDetails.setTime(timeOfPreviousEvent.plusMinutes(5));
                    emptyFiveMinutesDetails.setVolume(0l);
                    emptyFiveMinutesDetails.setCumulatedVolume(stockFiveMinutesDetailsList.get(previousElementToCompare).getCumulatedVolume());
                    emptyFiveMinutesDetails.setStock(element.getStock());
                    filledEmptyTimeAndCumulativeVolume.add(emptyFiveMinutesDetails);
                }
            }
            element.setCumulatedVolume(element.getVolume() + filledEmptyTimeAndCumulativeVolume.get(filledEmptyTimeAndCumulativeVolume.size() - 1).getCumulatedVolume());
            filledEmptyTimeAndCumulativeVolume.add(element);
            i++;
        }
        return filledEmptyTimeAndCumulativeVolume;
    }
}
