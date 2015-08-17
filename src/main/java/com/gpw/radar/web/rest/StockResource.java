package com.gpw.radar.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.gpw.radar.domain.Stock;
import com.gpw.radar.domain.User;
import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.domain.enumeration.TrendDirection;
import com.gpw.radar.repository.StockRepository;
import com.gpw.radar.repository.UserRepository;
import com.gpw.radar.security.AuthoritiesConstants;
import com.gpw.radar.web.rest.util.PaginationUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Stock.
 */
@RestController
@RequestMapping("/api")
public class StockResource {

	private final Logger log = LoggerFactory.getLogger(StockResource.class);

	@Inject
	private StockRepository stockRepository;

	@Inject
	private UserRepository userRepository;

	/**
	 * POST /stocks -> Create a new stock.
	 */
	@RequestMapping(value = "/stocks", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@RolesAllowed(AuthoritiesConstants.ADMIN)
	public ResponseEntity<Void> create(@Valid @RequestBody Stock stock) throws URISyntaxException {
		log.debug("REST request to save Stock : {}", stock);
		if (stock.getId() != null) {
			return ResponseEntity.badRequest().header("Failure", "A new stock cannot already have an ID").build();
		}
		stockRepository.save(stock);
		return ResponseEntity.created(new URI("/api/stocks/" + stock.getId())).build();
	}

	/**
	 * PUT /stocks -> Updates an existing stock.
	 */
	@RequestMapping(value = "/stocks", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@RolesAllowed(AuthoritiesConstants.ADMIN)
	public ResponseEntity<Void> update(@Valid @RequestBody Stock stock) throws URISyntaxException {
		log.debug("REST request to update Stock : {}", stock);
		if (stock.getId() == null) {
			return create(stock);
		}
		stockRepository.save(stock);
		return ResponseEntity.ok().build();
	}

	/**
	 * GET /stocks -> get all the stocks.
	 */
	@RequestMapping(value = "/stocks", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<Stock>> getAll(@RequestParam(value = "page", required = false) Integer offset,
			@RequestParam(value = "per_page", required = false) Integer limit) throws URISyntaxException {
		Page<Stock> page = stockRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/stocks", offset, limit);
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	/**
	 * GET /stocks/:id -> get the "id" stock.
	 */
	@RequestMapping(value = "/stocks/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Stock> get(@PathVariable Long id) {
		log.debug("REST request to get Stock : {}", id);
		return Optional.ofNullable(stockRepository.findOne(id)).map(stock -> new ResponseEntity<>(stock, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /stocks/:id -> delete the "id" stock.
	 */
	@RequestMapping(value = "/stocks/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@RolesAllowed(AuthoritiesConstants.ADMIN)
	public void delete(@PathVariable Long id) {
		log.debug("REST request to delete Stock : {}", id);
		stockRepository.delete(id);
	}

	@RequestMapping(value = "/stocks/get/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Stock> gettAllWithOutPagination() {
		return stockRepository.findAll();
	}

//	@RequestMapping(value = "/stocks/trends/{direction}/days", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<List<Stock>> getStocksTrend(@PathVariable String direction, @RequestParam int days, @RequestParam(value = "page") Integer offset,
//      @RequestParam(value = "per_page") Integer limit) throws URISyntaxException {
//		TrendDirection trendDirection = TrendDirection.valueOf(direction.toUpperCase());
//		switch (trendDirection) {
//		case UP:
//			switch (days) {
//			case 10:
//				Page<Stock> stockIn10DaysTrendUp = stockRepository.findWithStocksIndicators10DaysTrendUp(PaginationUtil.generatePageRequest(offset, limit));
//                HttpHeaders headers10DaysTrendUp = PaginationUtil.generatePaginationHttpHeaders(stockIn10DaysTrendUp, "/apis/tocks/trends/up/days", offset, limit);
//                return new ResponseEntity<>(stockIn10DaysTrendUp.getContent(), headers10DaysTrendUp, HttpStatus.OK);
//			case 30:
//				Page<Stock> stockIn30DaysTrendUp = stockRepository.findWithStocksIndicators30DaysTrendUp(PaginationUtil.generatePageRequest(offset, limit));
//                HttpHeaders headers30DaysTrendUp = PaginationUtil.generatePaginationHttpHeaders(stockIn30DaysTrendUp, "/apis/tocks/trends/up/days", offset, limit);
//                return new ResponseEntity<>(stockIn30DaysTrendUp.getContent(), headers30DaysTrendUp, HttpStatus.OK);
//			case 60:
//				Page<Stock> stockIn60DaysTrendUp = stockRepository.findWithStocksIndicators60DaysTrendUp(PaginationUtil.generatePageRequest(offset, limit));
//                HttpHeaders headers60DaysTrendUp = PaginationUtil.generatePaginationHttpHeaders(stockIn60DaysTrendUp, "/apis/tocks/trends/up/days", offset, limit);
//                return new ResponseEntity<>(stockIn60DaysTrendUp.getContent(), headers60DaysTrendUp, HttpStatus.OK);
//			case 90:
//				Page<Stock> stockIn90DaysTrendUp = stockRepository.findWithStocksIndicators90DaysTrendUp(PaginationUtil.generatePageRequest(offset, limit));
//                HttpHeaders headers90DaysTrendUp = PaginationUtil.generatePaginationHttpHeaders(stockIn90DaysTrendUp, "/apis/tocks/trends/up/days", offset, limit);
//                return new ResponseEntity<>(stockIn90DaysTrendUp.getContent(), headers90DaysTrendUp, HttpStatus.OK);
//			default:
//				break;
//			}
//		case DOWN:
//			switch (days) {
//			case 10:
//				Page<Stock> stockIn10DaysTrendDown = stockRepository.findWithStocksIndicators10DaysTrendDown(PaginationUtil.generatePageRequest(offset, limit));
//                HttpHeaders headers10DaysTrendDown = PaginationUtil.generatePaginationHttpHeaders(stockIn10DaysTrendDown, "/apis/tocks/trends/down/days", offset, limit);
//                return new ResponseEntity<>(stockIn10DaysTrendDown.getContent(), headers10DaysTrendDown, HttpStatus.OK);
//			case 30:
//				Page<Stock> stockIn30DaysTrendDown = stockRepository.findWithStocksIndicators30DaysTrendDown(PaginationUtil.generatePageRequest(offset, limit));
//                HttpHeaders headers30DaysTrendDown = PaginationUtil.generatePaginationHttpHeaders(stockIn30DaysTrendDown, "/apis/tocks/trends/down/days", offset, limit);
//                return new ResponseEntity<>(stockIn30DaysTrendDown.getContent(), headers30DaysTrendDown, HttpStatus.OK);
//			case 60:
//				Page<Stock> stockIn60DaysTrendDown = stockRepository.findWithStocksIndicators60DaysTrendDown(PaginationUtil.generatePageRequest(offset, limit));
//                HttpHeaders headers60DaysTrendDown = PaginationUtil.generatePaginationHttpHeaders(stockIn60DaysTrendDown, "/apis/tocks/trends/down/days", offset, limit);
//                return new ResponseEntity<>(stockIn60DaysTrendDown.getContent(), headers60DaysTrendDown, HttpStatus.OK);
//			case 90:
//				Page<Stock> stockIn90DaysTrendDown = stockRepository.findWithStocksIndicators90DaysTrendDown(PaginationUtil.generatePageRequest(offset, limit));
//                HttpHeaders headers90DaysTrendDown = PaginationUtil.generatePaginationHttpHeaders(stockIn90DaysTrendDown, "/apis/tocks/trends/down/days", offset, limit);
//                return new ResponseEntity<>(stockIn90DaysTrendDown.getContent(), headers90DaysTrendDown, HttpStatus.OK);
//			default:
//				break;
//			}
//		default:
//			break;
//		}
//		return null;
//	}

	@RequestMapping(value = "/stock/follow/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public void followStock(@PathVariable Long id, Principal principal) {
        Stock stock = stockRepository.findOne(id);
        String name = principal.getName();
        Optional<User> user = userRepository.findOneByLogin(name);
        User activeUser = user.get();
        activeUser.getStocks().add(stock);
        userRepository.save(activeUser);
    }

	@RequestMapping(value = "/stock/stop/follow/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public void stopFollowStock(@PathVariable Long id, Principal principal) {
		Stock stock = stockRepository.findOne(id);
		String name = principal.getName();
		Optional<User> user = userRepository.findOneByLogin(name);
		User activeUser = user.get();
		activeUser.getStocks().remove(stock);
		userRepository.save(activeUser);
	}

    @RequestMapping(value = "/get/tickers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public EnumSet<StockTicker> getAllTickers() {
        return EnumSet.allOf(StockTicker.class);
    }
}
