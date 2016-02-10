package com.gpw.radar.service.stock;

import com.gpw.radar.domain.User;
import com.gpw.radar.domain.enumeration.TrendDirection;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockIndicators;
import com.gpw.radar.repository.UserRepository;
import com.gpw.radar.repository.stock.StockIndicatorsRepository;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.service.UserService;
import com.gpw.radar.service.parser.JsonViewConverter;
import com.gpw.radar.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.net.URISyntaxException;
import java.util.List;

@Service
public class StockService {

    private final Logger logger = LoggerFactory.getLogger(StockService.class);

	@Inject
	private StockRepository stockRepository;

	@Inject
	private StockIndicatorsRepository stockIndicatorsRepository;

	@Inject
	private UserService userService;

	@Inject
	private UserRepository userRepository;

    @Inject
    private JsonViewConverter jsonViewConverter;

	public ResponseEntity<List<Stock>> getAllWithPagination(int offset, int limit) throws URISyntaxException {
		Page<Stock> page = stockRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/stocks", offset, limit);
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	public ResponseEntity<String> getAllStocksFetchStockIndicators() throws URISyntaxException {
		List<StockIndicators> stockIndicators = stockIndicatorsRepository.findAllStocksFetchStockIndicators();
		String jsonResult = jsonViewConverter.convertStockIndicatorsToJsonView(stockIndicators);
		return new ResponseEntity<String>(jsonResult, HttpStatus.OK);
	}

	public ResponseEntity<String> getTrendingStocks(TrendDirection trendDirection, int days, int offset, int limit) throws URISyntaxException {
		String jsonResult = "";
		switch (trendDirection) {
		case UP:
			switch (days) {
			case 10:
				Page<StockIndicators> stockIn10DaysTrendUp = stockIndicatorsRepository.findWithStocksIndicators10DaysTrendUp(PaginationUtil.generatePageRequest(offset, limit));
				jsonResult = jsonViewConverter.convertStockIndicatorsToJsonView(stockIn10DaysTrendUp.getContent());
				HttpHeaders headers10DaysTrendUp = PaginationUtil.generatePaginationHttpHeaders(stockIn10DaysTrendUp, "/apis/tocks/trends/up/days", offset, limit);
				return new ResponseEntity<String>(jsonResult, headers10DaysTrendUp, HttpStatus.OK);
			case 30:
				Page<StockIndicators> stockIn30DaysTrendUp = stockIndicatorsRepository.findWithStocksIndicators30DaysTrendUp(PaginationUtil.generatePageRequest(offset, limit));
				jsonResult = jsonViewConverter.convertStockIndicatorsToJsonView(stockIn30DaysTrendUp.getContent());
				HttpHeaders headers30DaysTrendUp = PaginationUtil.generatePaginationHttpHeaders(stockIn30DaysTrendUp, "/apis/tocks/trends/up/days", offset, limit);
				return new ResponseEntity<String>(jsonResult, headers30DaysTrendUp, HttpStatus.OK);
			case 60:
				Page<StockIndicators> stockIn60DaysTrendUp = stockIndicatorsRepository.findWithStocksIndicators60DaysTrendUp(PaginationUtil.generatePageRequest(offset, limit));
				jsonResult = jsonViewConverter.convertStockIndicatorsToJsonView(stockIn60DaysTrendUp.getContent());
				HttpHeaders headers60DaysTrendUp = PaginationUtil.generatePaginationHttpHeaders(stockIn60DaysTrendUp, "/apis/tocks/trends/up/days", offset, limit);
				return new ResponseEntity<String>(jsonResult, headers60DaysTrendUp, HttpStatus.OK);
			case 90:
				Page<StockIndicators> stockIn90DaysTrendUp = stockIndicatorsRepository.findWithStocksIndicators90DaysTrendUp(PaginationUtil.generatePageRequest(offset, limit));
				jsonResult = jsonViewConverter.convertStockIndicatorsToJsonView(stockIn90DaysTrendUp.getContent());
				HttpHeaders headers90DaysTrendUp = PaginationUtil.generatePaginationHttpHeaders(stockIn90DaysTrendUp, "/apis/tocks/trends/up/days", offset, limit);
				return new ResponseEntity<String>(jsonResult, headers90DaysTrendUp, HttpStatus.OK);
			default:
				break;
			}
		case DOWN:
			switch (days) {
			case 10:
				Page<StockIndicators> stockIn10DaysTrendDown = stockIndicatorsRepository.findWithStocksIndicators10DaysTrendDown(PaginationUtil.generatePageRequest(offset, limit));
				jsonResult = jsonViewConverter.convertStockIndicatorsToJsonView(stockIn10DaysTrendDown.getContent());
				HttpHeaders headers10DaysTrendDown = PaginationUtil.generatePaginationHttpHeaders(stockIn10DaysTrendDown, "/apis/tocks/trends/down/days", offset, limit);
				return new ResponseEntity<String>(jsonResult, headers10DaysTrendDown, HttpStatus.OK);
			case 30:
				Page<StockIndicators> stockIn30DaysTrendDown = stockIndicatorsRepository.findWithStocksIndicators30DaysTrendDown(PaginationUtil.generatePageRequest(offset, limit));
				jsonResult = jsonViewConverter.convertStockIndicatorsToJsonView(stockIn30DaysTrendDown.getContent());
				HttpHeaders headers30DaysTrendDown = PaginationUtil.generatePaginationHttpHeaders(stockIn30DaysTrendDown, "/apis/tocks/trends/down/days", offset, limit);
				return new ResponseEntity<String>(jsonResult, headers30DaysTrendDown, HttpStatus.OK);
			case 60:
				Page<StockIndicators> stockIn60DaysTrendDown = stockIndicatorsRepository.findWithStocksIndicators60DaysTrendDown(PaginationUtil.generatePageRequest(offset, limit));
				jsonResult = jsonViewConverter.convertStockIndicatorsToJsonView(stockIn60DaysTrendDown.getContent());
				HttpHeaders headers60DaysTrendDown = PaginationUtil.generatePaginationHttpHeaders(stockIn60DaysTrendDown, "/apis/tocks/trends/down/days", offset, limit);
				return new ResponseEntity<String>(jsonResult, headers60DaysTrendDown, HttpStatus.OK);
			case 90:
				Page<StockIndicators> stockIn90DaysTrendDown = stockIndicatorsRepository.findWithStocksIndicators90DaysTrendDown(PaginationUtil.generatePageRequest(offset, limit));
				jsonResult = jsonViewConverter.convertStockIndicatorsToJsonView(stockIn90DaysTrendDown.getContent());
				HttpHeaders headers90DaysTrendDown = PaginationUtil.generatePaginationHttpHeaders(stockIn90DaysTrendDown, "/apis/tocks/trends/down/days", offset, limit);
				return new ResponseEntity<String>(jsonResult, headers90DaysTrendDown, HttpStatus.OK);
			}
		}
		return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
	}

	@Transactional
	public ResponseEntity<Void> followStock(long stockId) throws URISyntaxException {
		Stock stock = stockRepository.findOne(stockId);
		User user = userService.getUserWithAuthorities();
		user.getStocks().add(stock);
		userRepository.save(user);
        return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@Transactional
	public ResponseEntity<Void> stopFollowStock(long stockId) throws URISyntaxException {
		Stock stock = stockRepository.findOne(stockId);
		User user = userService.getUserWithAuthorities();
		user.getStocks().remove(stock);
		userRepository.save(user);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
}
