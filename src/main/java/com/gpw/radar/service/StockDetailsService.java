package com.gpw.radar.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gpw.radar.domain.Stock;
import com.gpw.radar.domain.StockDetails;
import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.repository.StockDetailsRepository;

@Service
@Transactional
public class StockDetailsService {

	private final Logger log = LoggerFactory.getLogger(StockDetailsService.class);

	@Autowired
	private StockDetailsRepository stockDetailsRepository;

	@Autowired
	private WebParserService parserService;

	@Autowired
	private StockService stockService;

	public void saveOrUpdate(StockDetails stockDetails) {
		stockDetailsRepository.save(stockDetails);
	}

	public List<StockDetails> getByStockTickerOrderByDateDesc(StockTicker ticker) {
		return stockDetailsRepository.findByStockTickerOrderByDateDesc(ticker);
	}

	public List<StockDetails> getByStocTickerkOrderByDateAsc(StockTicker ticker) {
		return stockDetailsRepository.findByStockTickerOrderByDateAsc(ticker);
	}

	public StockDetails findTopByDate() {
		return stockDetailsRepository.findTopByOrderByDateDesc();
	}

	public List<StockDetails> getByTicker(StockTicker ticker) {
		return stockDetailsRepository.findByStockTicker(ticker);
	}

	public List<StockDetails> findByStock(Stock stock) {
		return stockDetailsRepository.findByStock(stock);
	}

	public void updateStockDetails(Stock stock, LocalDate wig20Date) {
		String line = "";
		String cvsSplitBy = ",";
		try {
			URLConnection yc = null;
			URL urlStooq = new URL("http://stooq.pl/q/l/?s=" + stock.getTicker() + "&f=sd2t2ohlcv&h&e=csv");
			yc = urlStooq.openConnection();

			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
			in.readLine();
			while ((line = in.readLine()) != null) {
				StockDetails stockDetails = new StockDetails();
				stockDetails.setStock(stock);
				String[] stockDetailsFromCsv = line.split(cvsSplitBy);
				
				if(isQuotesToUpdate(wig20Date, stockDetailsFromCsv)){
					setNewValuesOfStockDetails(stockDetails, stockDetailsFromCsv);
				}
				else{
					setLastValuesOfStockDetails(stock, wig20Date, stockDetails);
				}
				saveOrUpdate(stockDetails);
			}

		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean isQuotesToUpdate(LocalDate wig20Date, String[] stockDetailsFromCsv) {
		return wig20Date.isEqual(parserService.parseLocalDateFromString(stockDetailsFromCsv[1]));
	}

	private void setNewValuesOfStockDetails(StockDetails stockDetails, String[] stockDetailsFromCsv) {
		stockDetails.setDate(parserService.parseLocalDateFromString(stockDetailsFromCsv[1]));
		stockDetails.setOpenPrice(new BigDecimal(stockDetailsFromCsv[3]));
		stockDetails.setMaxPrice(new BigDecimal(stockDetailsFromCsv[4]));
		stockDetails.setMinPrice(new BigDecimal(stockDetailsFromCsv[5]));
		stockDetails.setClosePrice(new BigDecimal(stockDetailsFromCsv[6]));
		try {
			stockDetails.setVolume(Long.valueOf(stockDetailsFromCsv[7]));
		} catch (ArrayIndexOutOfBoundsException exc) {
			stockDetails.setVolume(0l);
		}
	}

	private void setLastValuesOfStockDetails(Stock stock, LocalDate wig20Date, StockDetails stockDetails) {
		stockDetails.setDate(wig20Date);
		StockDetails lastStockDetails = stockDetailsRepository.findTopByStockOrderByDateDesc(stock);
		stockDetails.setOpenPrice(lastStockDetails.getClosePrice());
		stockDetails.setMaxPrice(lastStockDetails.getClosePrice());
		stockDetails.setMinPrice(lastStockDetails.getClosePrice());
		stockDetails.setClosePrice(lastStockDetails.getClosePrice());
		stockDetails.setVolume(0l);
	}

	public LocalDate getLastDateWig20FromStooq() {
		String line = "";
		String cvsSplitBy = ",";
		LocalDate date = null;
		try {
			URL urlStooq = new URL("http://stooq.pl/q/l/?s=wig20&f=sd2t2ohlcv&h&e=csv");
			URLConnection yc = urlStooq.openConnection();

			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
			in.readLine();
			line = in.readLine();
			String[] stockDetailsFromCsv = line.split(cvsSplitBy);
			date = parserService.parseLocalDateFromString(stockDetailsFromCsv[1]);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return date;
	}
}