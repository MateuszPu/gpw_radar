package com.gpw.radar.service;

import javax.inject.Inject;

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
}
