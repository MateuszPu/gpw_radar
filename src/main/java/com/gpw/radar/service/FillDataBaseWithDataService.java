package com.gpw.radar.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gpw.radar.domain.Stock;
import com.gpw.radar.domain.StockDetails;
import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.repository.StockRepository;

@Service
@Transactional
public class FillDataBaseWithDataService {

	private final Logger log = LoggerFactory.getLogger(FillDataBaseWithDataService.class);

	@Inject
	private StockRepository stockRepository;

	@Inject
	private WebParserService parserService;

	public void fillDataBaseWithStocks() throws IOException {
		for (StockTicker element : StockTicker.values()) {
			Stock stock = new Stock();
			String stockTicker = element.name();
			stock.setTicker(element);
			String nameOfStock = parserService.getNameOfStock(stockTicker);
			String shortNameOfStock = parserService.getShortNameOfStock(stockTicker);
			stock.setStockName(nameOfStock);
			stock.setStockShortName(shortNameOfStock);
			stockRepository.save(stock);
		}
	}

	public Set<StockDetails> parseStockDetailsByStockFromFile(Stock stock) {
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

				stockDetails.setDate(parserService.parseLocalDateFromString(stockdetails[0]));
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