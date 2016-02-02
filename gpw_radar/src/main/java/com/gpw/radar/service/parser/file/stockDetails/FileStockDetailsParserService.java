package com.gpw.radar.service.parser.file.stockDetails;

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
public class FileStockDetailsParserService implements StockDetailsParser {

    private final Logger logger = LoggerFactory.getLogger(FileStockDetailsParserService.class);

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
}
