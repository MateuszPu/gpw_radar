package com.gpw.radar.service.auto.update.stockDetails;

import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.repository.stock.StockDetailsRepository;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// getting data from http://stooq.pl/
@Component("stooqParser")
public class StooqParser implements StockDetailsParser {

    @Inject
    private StockRepository stockRepository;

    @Inject
    private StockDetailsRepository stockDetailsRepository;

    @Inject
    private DateAndTimeParserService dateAndTimeParserService;

    @Inject
    private CurrentStockDetailsParserService currentStockDetailsParserService;

    private final Logger logger = LoggerFactory.getLogger(StooqParser.class);

    private static final String cvsSplitBy = ",";
    private LocalDate quotesDate;

    @Override
    public List<StockDetails> getCurrentStockDetails() {
        List<StockDetails> stockDetails = new ArrayList<StockDetails>();

        for (StockTicker element : StockTicker.values()) {
            Stock stock = stockRepository.findByTicker(element);
            String line = "";

            String url = ("http://stooq.pl/q/l/?s=" + stock.getTicker() + "&f=sd2t2ohlcv&h&e=csv");
            InputStreamReader inputStreamReader = currentStockDetailsParserService.getInputStreamReaderFromUrl(url);

            // skip headers
            try (BufferedReader in = new BufferedReader(inputStreamReader)) {
                in.readLine();
                line = in.readLine();
            } catch (IOException e) {
                logger.error("Error occurs: " + e.getMessage());
            }
            StockDetails std = new StockDetails();
            String[] stockDetailsFromCsv = line.split(cvsSplitBy);

            if (isQuotesUpToDate(quotesDate, stockDetailsFromCsv)) {
                std = getNewValuesOfStockDetails(std, stockDetailsFromCsv);
            } else {
                std = getLastValuesOfStockDetails(std, stock, quotesDate);
            }
            std.setStock(stock);
            stockDetails.add(std);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return stockDetails;
    }

    private boolean isQuotesUpToDate(LocalDate wig20Date, String[] stockDetailsFromCsv) {
        return wig20Date.isEqual(dateAndTimeParserService.parseLocalDateFromString(stockDetailsFromCsv[1]));
    }

    private StockDetails getNewValuesOfStockDetails(StockDetails stockDetails, String[] stockDetailsFromCsv) {
        stockDetails.setDate(dateAndTimeParserService.parseLocalDateFromString(stockDetailsFromCsv[1]));
        stockDetails.setOpenPrice(new BigDecimal(stockDetailsFromCsv[3]));
        stockDetails.setMaxPrice(new BigDecimal(stockDetailsFromCsv[4]));
        stockDetails.setMinPrice(new BigDecimal(stockDetailsFromCsv[5]));
        stockDetails.setClosePrice(new BigDecimal(stockDetailsFromCsv[6]));
        try {
            stockDetails.setVolume(Long.valueOf(stockDetailsFromCsv[7]));
        } catch (ArrayIndexOutOfBoundsException exc) {
            stockDetails.setVolume(0l);
        }
        return stockDetails;
    }

    // use this method while stock was not quoted on market in current day
    private StockDetails getLastValuesOfStockDetails(StockDetails stockDetails, Stock stock, LocalDate wigCurrentQuotesDate) {
        stockDetails.setDate(wigCurrentQuotesDate);
        StockDetails lastStockDetails = stockDetailsRepository.findTopByStockOrderByDateDesc(stock);
        stockDetails.setOpenPrice(lastStockDetails.getClosePrice());
        stockDetails.setMaxPrice(lastStockDetails.getClosePrice());
        stockDetails.setMinPrice(lastStockDetails.getClosePrice());
        stockDetails.setClosePrice(lastStockDetails.getClosePrice());
        stockDetails.setVolume(0l);

        return stockDetails;
    }

    @Override
    public void setQutesDate(LocalDate quotesDate) {
        this.quotesDate = quotesDate;
    }

}
