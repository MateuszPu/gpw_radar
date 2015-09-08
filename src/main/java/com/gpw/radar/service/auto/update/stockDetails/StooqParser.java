package com.gpw.radar.service.auto.update.stockDetails;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gpw.radar.domain.Stock;
import com.gpw.radar.domain.StockDetails;
import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.repository.StockDetailsRepository;
import com.gpw.radar.repository.StockRepository;
import com.gpw.radar.service.WebParserService;

// getting data from http://stooq.pl/
@Component("stooqParser")
public class StooqParser implements StockDetailsParser {
	
	@Autowired
	private StockRepository stockRepository;
	
	@Autowired
	private StockDetailsRepository stockDetailsRepository;
	
	@Autowired
	private WebParserService webParserService;

	private static final String cvsSplitBy = ",";
	private LocalDate quotesDate;

	@Override
	public List<StockDetails> getCurrentStockDetails() {
		List<StockDetails> stockDetails = new ArrayList<StockDetails>();
		
		for (StockTicker element : StockTicker.values()) {
			Stock stock = stockRepository.findByTicker(element);

			String line = "";
			
			try {
				URLConnection stooqConnection = null;
				URL urlStooq = new URL("http://stooq.pl/q/l/?s=" + stock.getTicker() + "&f=sd2t2ohlcv&h&e=csv");
				stooqConnection = urlStooq.openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(stooqConnection.getInputStream()));

				// skip headers
				in.readLine();

				line = in.readLine();
				StockDetails std = new StockDetails();
				String[] stockDetailsFromCsv = line.split(cvsSplitBy);

				if (isQuotesUpToDate(quotesDate, stockDetailsFromCsv)) {
					std = getNewValuesOfStockDetails(std, stockDetailsFromCsv);
				} else {
					std = getLastValuesOfStockDetails(std, stock, quotesDate);
				}
				std.setStock(stock);
				stockDetails.add(std);

			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return stockDetails;
	}

	private boolean isQuotesUpToDate(LocalDate wig20Date, String[] stockDetailsFromCsv) {
		return wig20Date.isEqual(webParserService.parseLocalDateFromString(stockDetailsFromCsv[1]));
	}

	private StockDetails getNewValuesOfStockDetails(StockDetails stockDetails, String[] stockDetailsFromCsv) {
		stockDetails.setDate(webParserService.parseLocalDateFromString(stockDetailsFromCsv[1]));
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
