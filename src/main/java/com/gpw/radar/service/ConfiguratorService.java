package com.gpw.radar.service;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.gpw.radar.domain.database.FillDataStatus;
import com.gpw.radar.domain.database.Type;
import com.gpw.radar.repository.FillDataStatusRepository;
import com.gpw.radar.repository.StockDetailsRepository;
import com.gpw.radar.service.auto.update.stockDetails.ParserMethod;

@Service
public class ConfiguratorService {

	@Inject
	private StockDetailsRepository configuratorRepository;

	@Inject
	private FillDataStatusRepository fillDataStatusRepository;
	
	@Inject
	private FillDataBaseWithDataService fillDataBaseWithDataService;

	public void changeStockDetailsParserMethod(ParserMethod stockDetailsParserMethod) {
		configuratorRepository.setStockDetailsParserMethod(stockDetailsParserMethod.toString());
	}

	public ParserMethod getCurrentStockDetailsParserMethod() {
		ParserMethod currentMethod = configuratorRepository.findMethod();
		return currentMethod;
	}

	@Transactional
	public ResponseEntity<Void> setParserMethod(ParserMethod parserMethod) {
		configuratorRepository.setStockDetailsParserMethod(parserMethod.toString());
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	public ResponseEntity<List<FillDataStatus>> getFillDataStatus() {
		List<FillDataStatus> list = fillDataStatusRepository.findAll();
		return new ResponseEntity<List<FillDataStatus>>(list, HttpStatus.OK);
	}

	public ResponseEntity<Void> fillDatabaseWithData(Type type) {
		switch (type) {
		case STOCK:
			return fillDataBaseWithDataService.fillDataBaseWithStocks();
		case STOCK_DETAILS:
			return fillDataBaseWithDataService.fillDataBaseWithStockDetails();
		case STOCK_FINANCE_EVENTS:
			return fillDataBaseWithDataService.fillDataBaseWithStockFinanceEvent();
		default:
			return new ResponseEntity<Void> (HttpStatus.BAD_REQUEST);
		}
	}
}
