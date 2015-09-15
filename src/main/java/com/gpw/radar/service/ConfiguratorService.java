package com.gpw.radar.service;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.gpw.radar.repository.StockDetailsRepository;
import com.gpw.radar.service.auto.update.stockDetails.ParserMethod;

@Service
public class ConfiguratorService {
	
	@Inject
	private StockDetailsRepository configuratorRepository;
	
	public void changeStockDetailsParserMethod(ParserMethod stockDetailsParserMethod){
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
}
