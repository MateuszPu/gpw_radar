package com.gpw.radar.service.stock;

import com.gpw.radar.domain.stock.StockFinanceEvent;
import com.gpw.radar.repository.stock.StockFinanceEventRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class StockFinanceEventService {

	@Inject
	private StockFinanceEventRepository stockFinanceEventRepository;

	public ResponseEntity<List<StockFinanceEvent>> getAllStockFinanceEvent() {
		List<StockFinanceEvent> list = stockFinanceEventRepository.getAllFetchStock();
		return new ResponseEntity<List<StockFinanceEvent>>(list, HttpStatus.OK);
	}

}
