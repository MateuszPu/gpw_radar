package com.gpw.radar.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.gpw.radar.domain.Stock;
import com.gpw.radar.domain.StockIndicators;
import com.gpw.radar.domain.User;
import com.gpw.radar.domain.enumeration.TrendDirection;
import com.gpw.radar.repository.StockIndicatorsRepository;
import com.gpw.radar.repository.StockRepository;
import com.gpw.radar.repository.UserRepository;
import com.gpw.radar.web.rest.util.PaginationUtil;

@Service
public class StockService {

	private final Logger log = LoggerFactory.getLogger(StockService.class);

	@Inject
	private StockRepository stockRepository;
	
	@Inject
	private StockIndicatorsRepository stockIndicatorsRepository;

	@Inject
	private UserService userService;
	
	@Inject
    private UserRepository userRepository;

	public ResponseEntity<Void> create(Stock stock) throws URISyntaxException {
		log.debug("REST request to save Stock : {}", stock);
		if (stock.getId() != null) {
			return ResponseEntity.badRequest().header("Failure", "A new stock cannot already have an ID").build();
		}
		stockRepository.save(stock);
		return ResponseEntity.created(new URI("/api/stocks/" + stock.getId())).build();
	}

	public ResponseEntity<Void> update(Stock stock) throws URISyntaxException {
		log.debug("REST request to update Stock : {}", stock);
		if (stock.getId() == null) {
			return create(stock);
		}
		stockRepository.save(stock);
		return ResponseEntity.ok().build();
	}

	public ResponseEntity<List<Stock>> getAllWithPagination(int offset, int limit) throws URISyntaxException {
		Page<Stock> page = stockRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/stocks", offset, limit);
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	public ResponseEntity<List<StockIndicators>> getAllStocksFetchStockIndicators() throws URISyntaxException {
		List<StockIndicators> stocks = stockIndicatorsRepository.findAllStocksFetchStockIndicators();
		if (stocks == null) {
			return new ResponseEntity<List<StockIndicators>>(new ArrayList<StockIndicators>(), HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<StockIndicators>>(stocks, HttpStatus.OK);
	}
	
	public ResponseEntity<List<Stock>> getTrendingStocks(TrendDirection trendDirection, int days, int offset, int limit) throws URISyntaxException {
//		TrendDirection trendDirection = TrendDirection.valueOf(direction.toUpperCase());
		switch (trendDirection) {
		case UP:
			switch (days) {
			case 10:
				Page<Stock> stockIn10DaysTrendUp = stockRepository.findWithStocksIndicators10DaysTrendUp(PaginationUtil.generatePageRequest(offset, limit));
                HttpHeaders headers10DaysTrendUp = PaginationUtil.generatePaginationHttpHeaders(stockIn10DaysTrendUp, "/apis/tocks/trends/up/days", offset, limit);
                return new ResponseEntity<List<Stock>>(stockIn10DaysTrendUp.getContent(), headers10DaysTrendUp, HttpStatus.OK);
			case 30:
				Page<Stock> stockIn30DaysTrendUp = stockRepository.findWithStocksIndicators30DaysTrendUp(PaginationUtil.generatePageRequest(offset, limit));
                HttpHeaders headers30DaysTrendUp = PaginationUtil.generatePaginationHttpHeaders(stockIn30DaysTrendUp, "/apis/tocks/trends/up/days", offset, limit);
                return new ResponseEntity<List<Stock>>(stockIn30DaysTrendUp.getContent(), headers30DaysTrendUp, HttpStatus.OK);
			case 60:
				Page<Stock> stockIn60DaysTrendUp = stockRepository.findWithStocksIndicators60DaysTrendUp(PaginationUtil.generatePageRequest(offset, limit));
                HttpHeaders headers60DaysTrendUp = PaginationUtil.generatePaginationHttpHeaders(stockIn60DaysTrendUp, "/apis/tocks/trends/up/days", offset, limit);
                return new ResponseEntity<List<Stock>>(stockIn60DaysTrendUp.getContent(), headers60DaysTrendUp, HttpStatus.OK);
			case 90:
				Page<Stock> stockIn90DaysTrendUp = stockRepository.findWithStocksIndicators90DaysTrendUp(PaginationUtil.generatePageRequest(offset, limit));
                HttpHeaders headers90DaysTrendUp = PaginationUtil.generatePaginationHttpHeaders(stockIn90DaysTrendUp, "/apis/tocks/trends/up/days", offset, limit);
                return new ResponseEntity<List<Stock>>(stockIn90DaysTrendUp.getContent(), headers90DaysTrendUp, HttpStatus.OK);
			default:
				break;
			}
		case DOWN:
			switch (days) {
			case 10:
				Page<Stock> stockIn10DaysTrendDown = stockRepository.findWithStocksIndicators10DaysTrendDown(PaginationUtil.generatePageRequest(offset, limit));
                HttpHeaders headers10DaysTrendDown = PaginationUtil.generatePaginationHttpHeaders(stockIn10DaysTrendDown, "/apis/tocks/trends/down/days", offset, limit);
                return new ResponseEntity<List<Stock>>(stockIn10DaysTrendDown.getContent(), headers10DaysTrendDown, HttpStatus.OK);
			case 30:
				Page<Stock> stockIn30DaysTrendDown = stockRepository.findWithStocksIndicators30DaysTrendDown(PaginationUtil.generatePageRequest(offset, limit));
                HttpHeaders headers30DaysTrendDown = PaginationUtil.generatePaginationHttpHeaders(stockIn30DaysTrendDown, "/apis/tocks/trends/down/days", offset, limit);
                return new ResponseEntity<List<Stock>>(stockIn30DaysTrendDown.getContent(), headers30DaysTrendDown, HttpStatus.OK);
			case 60:
				Page<Stock> stockIn60DaysTrendDown = stockRepository.findWithStocksIndicators60DaysTrendDown(PaginationUtil.generatePageRequest(offset, limit));
                HttpHeaders headers60DaysTrendDown = PaginationUtil.generatePaginationHttpHeaders(stockIn60DaysTrendDown, "/apis/tocks/trends/down/days", offset, limit);
                return new ResponseEntity<List<Stock>>(stockIn60DaysTrendDown.getContent(), headers60DaysTrendDown, HttpStatus.OK);
			case 90:
				Page<Stock> stockIn90DaysTrendDown = stockRepository.findWithStocksIndicators90DaysTrendDown(PaginationUtil.generatePageRequest(offset, limit));
                HttpHeaders headers90DaysTrendDown = PaginationUtil.generatePaginationHttpHeaders(stockIn90DaysTrendDown, "/apis/tocks/trends/down/days", offset, limit);
                return new ResponseEntity<List<Stock>>(stockIn90DaysTrendDown.getContent(), headers90DaysTrendDown, HttpStatus.OK);
			}
		}
		return new ResponseEntity<List<Stock>>(new ArrayList<Stock>(), HttpStatus.NO_CONTENT);
	}
	
	@Transactional
	public ResponseEntity<Void> followStock(long stockId) throws URISyntaxException {
		Stock stock = stockRepository.findOne(stockId);
		User user = userService.getUserWithAuthorities();
		user.getStocks().add(stock);
		userRepository.save(user);
		return ResponseEntity.ok().build();
	}
	
	@Transactional
	public ResponseEntity<Void> stopFollowStock(long stockId) throws URISyntaxException {
		Stock stock = stockRepository.findOne(stockId);
		User user = userService.getUserWithAuthorities();
		user.getStocks().remove(stock);
		userRepository.save(user);
		return ResponseEntity.ok().build();
	}

}
