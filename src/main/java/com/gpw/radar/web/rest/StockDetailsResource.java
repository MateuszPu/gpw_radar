package com.gpw.radar.web.rest;

import java.io.IOException;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gpw.radar.service.StockDetailsService;

@RestController
@RequestMapping("/api")
public class StockDetailsResource {
	
	@Inject
	private StockDetailsService stockDetailsService;
	
	@RequestMapping(value = "/get/top/by/date", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LocalDate> getTopStockDetailsByDate() throws IOException
	{
		return stockDetailsService.findLastTopDate();
	}	

}