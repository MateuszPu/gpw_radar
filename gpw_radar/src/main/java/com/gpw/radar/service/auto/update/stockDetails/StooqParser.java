package com.gpw.radar.service.auto.update.stockDetails;

import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.repository.stock.StockDetailsRepository;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.service.parser.DateAndTimeParserService;
import com.gpw.radar.service.parser.web.UrlStreamsGetterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// getting data from http://stooq.pl/
@Component("stooqParser")
public class StooqParser implements StockDetailsParser {

    private static final int indexOfOpenPrice = 3;
    private static final int indexOfMaxPrice = 4;
    private static final int indexOfMinPrice = 5;
    private static final int indexOfClosePrice = 6;
    // private final int indexOfTransactionCount = 20;
    private static final int indexOfVolume = 7;

    @Inject
    private StockRepository stockRepository;

    @Inject
    private StockDetailsRepository stockDetailsRepository;

    @Inject
    private DateAndTimeParserService dateAndTimeParserService;

    @Inject
    private UrlStreamsGetterService urlStreamsGetterService;

    private final Logger logger = LoggerFactory.getLogger(StooqParser.class);

    private static final String cvsSplitBy = ",";
    private LocalDate quotesDate;

    @Override
    public List<StockDetails> getCurrentStockDetails() {
        List<StockDetails> stockDetails = new ArrayList<StockDetails>();
        List<Stock> allStocks = stockRepository.findAll();

        for (Stock stock : allStocks) {
            String url = ("http://stooq.pl/q/l/?s=" + stock.getTicker() + "&f=sd2t2ohlcv&h&e=csv");

            try (InputStream inputStream = urlStreamsGetterService.getInputStreamFromUrl(url)) {
                stockDetails.add(parseFromWeb(inputStream, stock));
            } catch (IOException e) {
                logger.error("Error occurs: " + e.getMessage());
            }
        }
        return stockDetails;
    }

    public StockDetails parseFromWeb(InputStream inputStream, Stock stock) throws IOException {
        StockDetails std = new StockDetails();

        try(BufferedReader in = new BufferedReader(new InputStreamReader(inputStream))) {
            // skip headers
            in.readLine();
            String line = in.readLine();
            String[] stockDetailsFromCsv = line.split(cvsSplitBy);

            if (isQuotesUpToDate(quotesDate, stockDetailsFromCsv)) {
                std = getNewValuesOfStockDetails(std, stockDetailsFromCsv);
            } else {
                std = getLastValuesOfStockDetails(std, stock, quotesDate);
            }
            std.setStock(stock);
        }
        return std;
    }

    private boolean isQuotesUpToDate(LocalDate wig20Date, String[] stockDetailsFromCsv) {
        return wig20Date.isEqual(dateAndTimeParserService.parseLocalDateFromString(stockDetailsFromCsv[1]));
    }

    private StockDetails getNewValuesOfStockDetails(StockDetails stockDetails, String[] stockDetailsFromCsv) {
        stockDetails.setDate(dateAndTimeParserService.parseLocalDateFromString(stockDetailsFromCsv[1]));
        stockDetails.setOpenPrice(new BigDecimal(stockDetailsFromCsv[indexOfOpenPrice]));
        stockDetails.setMaxPrice(new BigDecimal(stockDetailsFromCsv[indexOfMaxPrice]));
        stockDetails.setMinPrice(new BigDecimal(stockDetailsFromCsv[indexOfMinPrice]));
        stockDetails.setClosePrice(new BigDecimal(stockDetailsFromCsv[indexOfClosePrice]));
        if (stockDetailsFromCsv.length == 8) {
            stockDetails.setVolume(Long.valueOf(stockDetailsFromCsv[indexOfVolume]));
        } else {
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
    public void setQuotesDate(LocalDate quotesDate) {
        this.quotesDate = quotesDate;
    }

}
