package com.gpw.radar.web.rest;

import java.net.URISyntaxException;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gpw.radar.domain.Stock;
import com.gpw.radar.domain.StockIndicators;
import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.domain.enumeration.TrendDirection;
import com.gpw.radar.security.AuthoritiesConstants;
import com.gpw.radar.service.StockService;

/**
 * REST controller for managing Stock.
 */
@RestController
@RequestMapping("/api")
public class StockResource {

	@Inject
	private StockService stockService;

//	/**
//	 * POST /stocks -> Create a new stock.
//	 */
//	@RequestMapping(value = "/stocks", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//	@Timed
//	@RolesAllowed(AuthoritiesConstants.ADMIN)
//	public ResponseEntity<Void> create(@Valid @RequestBody Stock stock) throws URISyntaxException {
//		return stockService.create(stock);
//	}
//
//	/**
//	 * PUT /stocks -> Updates an existing stock.
//	 */
//	@RequestMapping(value = "/stocks", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
//	@Timed
//	@RolesAllowed(AuthoritiesConstants.ADMIN)
//	public ResponseEntity<Void> update(@Valid @RequestBody Stock stock) throws URISyntaxException {
//		return stockService.update(stock);
//	}
//
//	/**
//	 * GET /stocks -> get all the stocks.
//	 */
//	@RequestMapping(value = "/stocks", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@Timed
//	public ResponseEntity<List<Stock>> getAll(@RequestParam(value = "page", required = false) Integer offset,
//			@RequestParam(value = "per_page", required = false) Integer limit) throws URISyntaxException {
//		return stockService.getAllWithPagination(offset, limit);
//	}
//
//	/**
//	 * GET /stocks/:id -> get the "id" stock.
//	 */
//	@RequestMapping(value = "/stocks/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@Timed
//	public ResponseEntity<Stock> get(@PathVariable Long id) {
//		log.debug("REST request to get Stock : {}", id);
//		return Optional.ofNullable(stockRepository.findOne(id)).map(stock -> new ResponseEntity<>(stock, HttpStatus.OK))
//				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
//	}
//
//	/**
//	 * DELETE /stocks/:id -> delete the "id" stock.
//	 */
//	@RequestMapping(value = "/stocks/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
//	@Timed
//	@RolesAllowed(AuthoritiesConstants.ADMIN)
//	public void delete(@PathVariable Long id) {
//		log.debug("REST request to delete Stock : {}", id);
//		stockRepository.delete(id);
//	}

	@RequestMapping(value = "/stocks/get/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed(AuthoritiesConstants.USER)
	public ResponseEntity<List<StockIndicators>> getAllWithOutPagination() throws URISyntaxException {
		return stockService.getAllStocksFetchStockIndicators();
	}

	@RequestMapping(value = "/stocks/trends/{direction}/days", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed(AuthoritiesConstants.USER)
	public ResponseEntity<List<Stock>> getStocksTrend(@PathVariable TrendDirection direction, @RequestParam int days, @RequestParam(value = "page") Integer offset,
      @RequestParam(value = "per_page") Integer limit) throws URISyntaxException {
		return stockService.getTrendingStocks(direction, days, offset, limit);
	}

	@RequestMapping(value = "/stock/follow/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed(AuthoritiesConstants.USER)
	public ResponseEntity<Void> followStock(@PathVariable long id) throws URISyntaxException {
//        String name = principal.getName();
//        Optional<User> user = userRepository.findOneByLogin(name);
//        User activeUser = user.get();
//        activeUser.getStocks().add(stock);
//        userRepository.save(activeUser);
		return stockService.followStock(id);
    }

	@RequestMapping(value = "/stock/stop/follow/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed(AuthoritiesConstants.USER)
	public ResponseEntity<Void> stopFollowStock(@PathVariable long id) throws URISyntaxException {
//		Stock stock = stockRepository.findOne(id);
//		String name = principal.getName();
//		Optional<User> user = userRepository.findOneByLogin(name);
//		User activeUser = user.get();
//		activeUser.getStocks().remove(stock);
//		userRepository.save(activeUser);
		return stockService.stopFollowStock(id);
	}

    @RequestMapping(value = "/get/tickers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public EnumSet<StockTicker> getAllTickers() {
        return EnumSet.allOf(StockTicker.class);
    }
}
