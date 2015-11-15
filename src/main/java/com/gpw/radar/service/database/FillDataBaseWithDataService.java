package com.gpw.radar.service.database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import com.gpw.radar.domain.stock.StockFiveMinutesDetails;
import com.gpw.radar.repository.stock.StockFiveMinutesDetailsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gpw.radar.domain.database.Type;
import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.repository.auto.update.FillDataStatusRepository;
import com.gpw.radar.repository.stock.StockDetailsRepository;
import com.gpw.radar.repository.stock.StockRepository;

@Service
public class FillDataBaseWithDataService {

	@Inject
	private StockRepository stockRepository;

	@Inject
	private WebParserService webParserService;

	@Inject
	private StockDetailsRepository stockDetailsRepository;

	@Inject
	private FillDataStatusRepository fillDataStatusRepository;

    @Inject
    private StockFiveMinutesDetailsRepository stockFiveMinutesDetailsRepository;

    @Inject
    private StockDetailsTextFileParserService parseStockDetailsByStockFromTxtFile;

	private int step;

	@Transactional
	public ResponseEntity<Void> fillDataBaseWithStocks() {
		step = 0;
		for (StockTicker element : StockTicker.values()) {
			Stock stock = new Stock();
			stock.setTicker(element);
			stock = webParserService.setNameAndShortName(stock);
			stockRepository.save(stock);
			increaseStep();
		}
		fillDataStatusRepository.updateType(Type.STOCK.toString());
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@Transactional
	public ResponseEntity<Void> fillDataBaseWithStockDetails() {
		step = 0;
		EnumSet<StockTicker> tickers = EnumSet.allOf(StockTicker.class);
		ExecutorService executor = Executors.newFixedThreadPool(4);

		for (StockTicker ticker : tickers) {
			executor.execute(() -> {
				Stock stock = stockRepository.findByTicker(ticker);
				Set<StockDetails> stockDetails = parseStockDetailsByStockFromTxtFile.parseStockDetailsByStockFromTxtFile(stock);
				stockDetailsRepository.save(stockDetails);
				increaseStep();
			});
		}

		executor.shutdown();
		try {
			executor.awaitTermination(5, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		fillDataStatusRepository.updateType(Type.STOCK_DETAILS.toString());
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

    @Transactional
    public ResponseEntity<Void> fillDataBaseWithStockFiveMinutesDetails() {
        step = 0;
        EnumSet<StockTicker> tickers = EnumSet.allOf(StockTicker.class);
        ExecutorService executor = Executors.newFixedThreadPool(4);

        for (StockTicker ticker : tickers) {
            executor.execute(() -> {
                Stock stock = stockRepository.findByTicker(ticker);
                List<StockFiveMinutesDetails> stockDetails = parseStockDetailsByStockFromTxtFile.parseStockFiveMinutesDetailsByStockFromTxtFile(stock, LocalDate.of(2015, 10, 28));
                stockFiveMinutesDetailsRepository.save(stockDetails);
                increaseStep();
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        fillDataStatusRepository.updateType(Type.STOCK_DETAILS.toString());
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

	private synchronized void increaseStep() {
		step++;
	}

	@Transactional
	public ResponseEntity<Void> fillDataBaseWithStockFinanceEvent() {
		step = 0;
		for (StockTicker element : StockTicker.values()) {
			Stock stock = stockRepository.findByTicker(element);
			webParserService.getStockFinanceEvent(stock);
			increaseStep();
		}
		fillDataStatusRepository.updateType(Type.STOCK_FINANCE_EVENTS.toString());
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}
}
