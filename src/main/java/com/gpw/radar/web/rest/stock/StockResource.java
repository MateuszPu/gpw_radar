package com.gpw.radar.web.rest.stock;

import java.net.URISyntaxException;
import java.util.EnumSet;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.domain.enumeration.TrendDirection;
import com.gpw.radar.security.AuthoritiesConstants;
import com.gpw.radar.service.stock.StockService;

/**
 * REST controller for managing stocks
 */
@RestController
@RequestMapping("/api")
public class StockResource {

	@Inject
	private StockService stockService;
	
	@RequestMapping(value = "/stocks/get/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed(AuthoritiesConstants.USER)
	public ResponseEntity<String> getAllWithOutPagination() throws URISyntaxException {
		return stockService.getAllStocksFetchStockIndicators();
	}

	@RequestMapping(value = "/stocks/trends/{direction}/days", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed(AuthoritiesConstants.USER)
	public ResponseEntity<String> getStocksTrend(@PathVariable TrendDirection direction, @RequestParam int days, @RequestParam(value = "page") Integer offset,
      @RequestParam(value = "per_page") Integer limit) throws URISyntaxException {
		return stockService.getTrendingStocks(direction, days, offset, limit);
	}

	@RequestMapping(value = "/stock/follow/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed(AuthoritiesConstants.USER)
	public ResponseEntity<Void> followStock(@PathVariable long id) throws URISyntaxException {
		return stockService.followStock(id);
    }

	@RequestMapping(value = "/stock/stop/follow/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed(AuthoritiesConstants.USER)
	public ResponseEntity<Void> stopFollowStock(@PathVariable long id) throws URISyntaxException {
		return stockService.stopFollowStock(id);
	}

    @RequestMapping(value = "/get/tickers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EnumSet<StockTicker>> getAllTickers() {
        return new ResponseEntity<EnumSet<StockTicker>>(EnumSet.allOf(StockTicker.class), HttpStatus.OK);
    }
}
