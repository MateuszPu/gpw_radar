package com.gpw.radar.web.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gpw.radar.domain.Stock;
import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.repository.StockRepository;
import com.gpw.radar.security.AuthoritiesConstants;
import com.gpw.radar.service.FillDatabaseWithDataService;
import com.gpw.radar.service.StockDetailsService;
import com.gpw.radar.service.WebParserService;

@RestController
@RequestMapping("/api/prepare/app")
@PreAuthorize("hasRole('" + AuthoritiesConstants.ADMIN + "')")
public class FillDatabaseWithData {

    @Inject
    private StockRepository stockRepository;

    @Inject
    private StockDetailsService stockDetailsService;

    @Inject
    private WebParserService webParserService;

    @Inject
    private FillDatabaseWithDataService fillDataPrepareWebAppService;

    private List<StockTicker> gpwStockTickers = new ArrayList<StockTicker>(Arrays.asList(StockTicker.values()));

    @RequestMapping(value = "/1")
    public void fillDataBaseWithStocks() throws IOException {
        fillDataPrepareWebAppService.fillDataBaseWithStocks();
    }

    @RequestMapping(value = "/2")
    public void fillDataBaseWithStocksDetails() {
        long start = System.currentTimeMillis();
        Thread threadOne = new Thread(new Runnable() {
            public void run() {
                parseFromTxtFile(0, 4);
            }
        });

        Thread threadTwo = new Thread(new Runnable() {
            public void run() {
                parseFromTxtFile(1, 4);
            }
        });

        Thread threadThree = new Thread(new Runnable() {
            public void run() {
                parseFromTxtFile(2, 4);
            }
        });

        Thread threadFour = new Thread(new Runnable() {
            public void run() {
                parseFromTxtFile(3, 4);
            }
        });

        threadOne.start();
        threadTwo.start();
        threadThree.start();
        threadFour.start();

        try {
            threadOne.join();
            threadTwo.join();
            threadThree.join();
            threadFour.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();
        System.out.println("czas potrzebny: 239.246: " + (end - start) / 1000.0);
    }

    private void parseFromTxtFile(int start, int increment) {
        for (int index = start; index < gpwStockTickers.size(); index += increment) {
        	StockTicker ticker = StockTicker.valueOf(gpwStockTickers.get(index).name());
            getStockDetailsForStockByTicker(ticker);
        }
    }

    private void getStockDetailsForStockByTicker(StockTicker ticker) {
        Stock stock = stockRepository.findByTicker(ticker);
        stock.setStockDetails(fillDataPrepareWebAppService.dataStockDetailsParserByTickerFromFile(ticker));
        stockRepository.save(stock);
    }

    @RequestMapping(value = "/3")
    public void fillDataBaseWithStockFinanceEvent() throws IOException {
        for (StockTicker element : StockTicker.values()) {
            Stock stock = stockRepository.findByTicker(element);
            webParserService.getStockFinanceEvent(stock);
        }
    }
}