package com.gpw.radar.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gpw.radar.domain.Stock;
import com.gpw.radar.domain.StockDetails;
import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.repository.StockDetailsRepository;
import com.gpw.radar.repository.StockRepository;

@Service
public class FillDataBaseWithDataService {

	private final Logger log = LoggerFactory.getLogger(FillDataBaseWithDataService.class);

	private final EnumSet<StockTicker> tickers = EnumSet.allOf(StockTicker.class);

	@Inject
	private StockRepository stockRepository;

	@Inject
	private WebParserService webParserService;

	@Inject
	private StockDetailsRepository stockDetailsRepository;

	@Transactional
	public ResponseEntity<Void> fillDataBaseWithStocks() throws IOException {
		for (StockTicker element : StockTicker.values()) {
			log.debug("Parsing stock for: " + element);
			Stock stock = new Stock();
			stock.setTicker(element);
			stock = webParserService.setNameAndShortName(stock);
			stockRepository.save(stock);
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
	@Transactional
	public ResponseEntity<Void> fillDataBaseWithStockDetails() {
		ExecutorService executor = Executors.newFixedThreadPool(4);

		for (StockTicker ticker : tickers) {
			executor.execute(() -> {
				Stock stock = stockRepository.findByTicker(ticker);
				Set<StockDetails> stockDetails = parseStockDetailsByStockFromFile(stock);
				stockDetailsRepository.save(stockDetails);
			});
		}

		executor.shutdown();
		try {
			executor.awaitTermination(5, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
	@Transactional
	public ResponseEntity<Void> fillDataBaseWithStockFinanceEvent() throws IOException {
        for (StockTicker element : StockTicker.values()) {
            Stock stock = stockRepository.findByTicker(element);
            webParserService.getStockFinanceEvent(stock);
        }
        return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@Transactional
	private Set<StockDetails> parseStockDetailsByStockFromFile(Stock stock) {
		log.debug("Parsing stock details for: " + stock.getStockName());
		String line = "";
		String cvsSplitBy = ",";
		Set<StockDetails> stockDetailList = new HashSet<StockDetails>();
		BufferedReader in = null;

		try {
			ClassLoader classLoader = getClass().getClassLoader();
			String filePath = "stocks_data/daily/pl/wse_stocks/" + stock.getTicker().name() + ".txt";
			FileReader fileIn = new FileReader(classLoader.getResource(filePath).getFile());

			in = new BufferedReader(fileIn);

			in.readLine();
			while ((line = in.readLine()) != null) {
				StockDetails stockDetails = new StockDetails();
				String[] stockdetails = line.split(cvsSplitBy);
				stockDetails.setStock(stock);

				stockDetails.setDate(webParserService.parseLocalDateFromString(stockdetails[0]));
				stockDetails.setOpenPrice(new BigDecimal(stockdetails[1]));
				stockDetails.setMaxPrice(new BigDecimal(stockdetails[2]));
				stockDetails.setMinPrice(new BigDecimal(stockdetails[3]));
				stockDetails.setClosePrice(new BigDecimal(stockdetails[4]));
				try {
					stockDetails.setVolume(Long.valueOf(stockdetails[5]));
				} catch (ArrayIndexOutOfBoundsException exc) {
					stockDetails.setVolume(0l);
				}
				stockDetailList.add(stockDetails);
			}

		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return stockDetailList;
	}
}