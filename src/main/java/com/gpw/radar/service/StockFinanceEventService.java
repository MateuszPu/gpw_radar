package com.gpw.radar.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.gpw.radar.domain.StockFinanceEvent;
import com.gpw.radar.repository.StockFinanceEventRepository;

@Service
public class StockFinanceEventService {
	
	@Inject
	private StockFinanceEventRepository stockFinanceEventRepository;
	
	public ResponseEntity<List<StockFinanceEvent>> getAllStockFinanceEvent() {
		List<StockFinanceEvent> list = stockFinanceEventRepository.getAllFetchStock();
		return new ResponseEntity<List<StockFinanceEvent>>(list, HttpStatus.OK);
	}

}
