package com.gpw.radar.service.stock;

import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.repository.stock.StockDetailsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;

@Service
public class StockDetailsService {

	@Inject
	private StockDetailsRepository stockDetailsRepository;

	public ResponseEntity<LocalDate> findLastTopDate() {
		StockDetails stockDetails = stockDetailsRepository.findTopByOrderByDateDesc();
		return new ResponseEntity<LocalDate>(stockDetails.getDate(), HttpStatus.OK);
	}

}