package com.gpw.radar.service.stock;

import com.gpw.radar.domain.User;
import com.gpw.radar.domain.enumeration.TrendDirection;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockIndicators;
import com.gpw.radar.repository.UserRepository;
import com.gpw.radar.repository.stock.StockIndicatorsRepository;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.service.UserService;
import com.gpw.radar.web.rest.dto.rssNews.NewsDetailsDTO;
import com.gpw.radar.web.rest.dto.stock.StockWithStockIndicatorsDTO;
import com.gpw.radar.web.rest.util.PaginationUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.lang.reflect.Type;
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

//	public ResponseEntity<List<Stock>> getAllWithPagination(int offset, int limit) throws URISyntaxException {
//		Page<Stock> page = stockRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
//		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/stocks", offset, limit);
//		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
//	}

	public ResponseEntity<List<StockWithStockIndicatorsDTO>> getAllStocksFetchStockIndicators() throws URISyntaxException {
		List<StockIndicators> stockIndicators = stockIndicatorsRepository.findAllStocksFetchStockIndicators();

        ModelMapper modelMapper = new ModelMapper();
        Type dtoType = new TypeToken<List<StockWithStockIndicatorsDTO>>() {}.getType();
        List<StockWithStockIndicatorsDTO> dto = modelMapper.map(stockIndicators, dtoType);

		return new ResponseEntity<List<StockWithStockIndicatorsDTO>>(dto, HttpStatus.OK);
	}

	public ResponseEntity<Page<StockIndicators>> getTrendingStocks(TrendDirection trendDirection, int days, int offset, int limit) throws URISyntaxException {
		String jsonResult = "";
		switch (trendDirection) {
		case UP:
			switch (days) {
			case 10:
				Page<StockIndicators> stockIn10DaysTrendUp = stockIndicatorsRepository.findWithStocksIndicators10DaysTrendUp(PaginationUtil.generatePageRequest(offset, limit));
				HttpHeaders headers10DaysTrendUp = PaginationUtil.generatePaginationHttpHeaders(stockIn10DaysTrendUp, "/apis/tocks/trends/up/days", offset, limit);
				return new ResponseEntity<Page<StockIndicators>>(stockIn10DaysTrendUp, headers10DaysTrendUp, HttpStatus.OK);
			case 30:
				Page<StockIndicators> stockIn30DaysTrendUp = stockIndicatorsRepository.findWithStocksIndicators30DaysTrendUp(PaginationUtil.generatePageRequest(offset, limit));
				HttpHeaders headers30DaysTrendUp = PaginationUtil.generatePaginationHttpHeaders(stockIn30DaysTrendUp, "/apis/tocks/trends/up/days", offset, limit);
				return new ResponseEntity<Page<StockIndicators>>(stockIn30DaysTrendUp, headers30DaysTrendUp, HttpStatus.OK);
			case 60:
				Page<StockIndicators> stockIn60DaysTrendUp = stockIndicatorsRepository.findWithStocksIndicators60DaysTrendUp(PaginationUtil.generatePageRequest(offset, limit));
				HttpHeaders headers60DaysTrendUp = PaginationUtil.generatePaginationHttpHeaders(stockIn60DaysTrendUp, "/apis/tocks/trends/up/days", offset, limit);
				return new ResponseEntity<Page<StockIndicators>>(stockIn60DaysTrendUp, headers60DaysTrendUp, HttpStatus.OK);
			case 90:
				Page<StockIndicators> stockIn90DaysTrendUp = stockIndicatorsRepository.findWithStocksIndicators90DaysTrendUp(PaginationUtil.generatePageRequest(offset, limit));
				HttpHeaders headers90DaysTrendUp = PaginationUtil.generatePaginationHttpHeaders(stockIn90DaysTrendUp, "/apis/tocks/trends/up/days", offset, limit);
				return new ResponseEntity<Page<StockIndicators>>(stockIn90DaysTrendUp, headers90DaysTrendUp, HttpStatus.OK);
			default:
				break;
			}
		case DOWN:
			switch (days) {
			case 10:
				Page<StockIndicators> stockIn10DaysTrendDown = stockIndicatorsRepository.findWithStocksIndicators10DaysTrendDown(PaginationUtil.generatePageRequest(offset, limit));
				HttpHeaders headers10DaysTrendDown = PaginationUtil.generatePaginationHttpHeaders(stockIn10DaysTrendDown, "/apis/tocks/trends/down/days", offset, limit);
				return new ResponseEntity<Page<StockIndicators>>(stockIn10DaysTrendDown, headers10DaysTrendDown, HttpStatus.OK);
			case 30:
				Page<StockIndicators> stockIn30DaysTrendDown = stockIndicatorsRepository.findWithStocksIndicators30DaysTrendDown(PaginationUtil.generatePageRequest(offset, limit));
				HttpHeaders headers30DaysTrendDown = PaginationUtil.generatePaginationHttpHeaders(stockIn30DaysTrendDown, "/apis/tocks/trends/down/days", offset, limit);
				return new ResponseEntity<Page<StockIndicators>>(stockIn30DaysTrendDown, headers30DaysTrendDown, HttpStatus.OK);
			case 60:
				Page<StockIndicators> stockIn60DaysTrendDown = stockIndicatorsRepository.findWithStocksIndicators60DaysTrendDown(PaginationUtil.generatePageRequest(offset, limit));
				HttpHeaders headers60DaysTrendDown = PaginationUtil.generatePaginationHttpHeaders(stockIn60DaysTrendDown, "/apis/tocks/trends/down/days", offset, limit);
				return new ResponseEntity<Page<StockIndicators>>(stockIn60DaysTrendDown, headers60DaysTrendDown, HttpStatus.OK);
			case 90:
				Page<StockIndicators> stockIn90DaysTrendDown = stockIndicatorsRepository.findWithStocksIndicators90DaysTrendDown(PaginationUtil.generatePageRequest(offset, limit));
				HttpHeaders headers90DaysTrendDown = PaginationUtil.generatePaginationHttpHeaders(stockIn90DaysTrendDown, "/apis/tocks/trends/down/days", offset, limit);
				return new ResponseEntity<Page<StockIndicators>>(stockIn90DaysTrendDown, headers90DaysTrendDown, HttpStatus.OK);
			}
		}
		return new ResponseEntity<Page<StockIndicators>>(HttpStatus.NO_CONTENT);
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
