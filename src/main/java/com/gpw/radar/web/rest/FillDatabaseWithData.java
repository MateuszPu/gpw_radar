package com.gpw.radar.web.rest;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gpw.radar.domain.Stock;
import com.gpw.radar.domain.StockDetails;
import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.repository.StockDetailsRepository;
import com.gpw.radar.repository.StockRepository;
import com.gpw.radar.security.AuthoritiesConstants;
import com.gpw.radar.service.FillDataBaseWithDataService;
import com.gpw.radar.service.WebParserService;

@RestController
@RequestMapping("/api/prepare/app")
@RolesAllowed(AuthoritiesConstants.ADMIN)
public class FillDatabaseWithData {

    @Inject
    private StockRepository stockRepository;

    @Inject
    private WebParserService webParserService;

    @Inject
    private FillDataBaseWithDataService fillDataBaseWithDataService;
    
    @Inject
    private StockDetailsRepository stockDetailsRepository;

    private final EnumSet<StockTicker> tickers = EnumSet.allOf(StockTicker.class);

    @RequestMapping(value = "/1")
    public void fillDataBaseWithStocks() throws IOException {
        fillDataBaseWithDataService.fillDataBaseWithStocks();
    }

    @RequestMapping(value = "/2")
    public void fillDataBaseWithStocksDetails() {
    	ExecutorService executor = Executors.newFixedThreadPool(4);
    	
    	for(StockTicker ticker: tickers){
    		executor.execute(() -> {
				Stock stock = stockRepository.findByTicker(ticker);
				Set<StockDetails> stockDetails = fillDataBaseWithDataService.parseStockDetailsByStockFromFile(stock);
				stockDetailsRepository.save(stockDetails);
			});
    	}
    	
		executor.shutdown();
		try {
			executor.awaitTermination(5, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }

    @RequestMapping(value = "/3")
    public void fillDataBaseWithStockFinanceEvent() throws IOException {
        for (StockTicker element : StockTicker.values()) {
            Stock stock = stockRepository.findByTicker(element);
            webParserService.getStockFinanceEvent(stock);
        }
    }
}