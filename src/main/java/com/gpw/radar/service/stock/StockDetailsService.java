package com.gpw.radar.service.stock;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.repository.stock.StockDetailsRepository;

@Service
public class StockDetailsService {

	@Inject
	private StockDetailsRepository stockDetailsRepository;

	public ResponseEntity<LocalDate> findLastTopDate() {
		StockDetails stockDetails = stockDetailsRepository.findTopByOrderByDateDesc();
		return new ResponseEntity<LocalDate>(stockDetails.getDate(), HttpStatus.OK);
	}
}