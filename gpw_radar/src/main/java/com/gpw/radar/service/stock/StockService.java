package com.gpw.radar.service.stock;

import com.gpw.radar.config.CacheConfiguration;
import com.gpw.radar.domain.User;
import com.gpw.radar.domain.enumeration.TrendDirection;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockIndicators;
import com.gpw.radar.repository.UserRepository;
import com.gpw.radar.repository.stock.StockDetailsRepository;
import com.gpw.radar.repository.stock.StockIndicatorsRepository;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.service.UserService;
import com.gpw.radar.web.rest.dto.stock.StockDTO;
import com.gpw.radar.web.rest.dto.stock.StockIndicatorsWithStocksDTO;
import com.gpw.radar.web.rest.dto.stock.StockWithStockIndicatorsDTO;
import com.gpw.radar.web.rest.util.PaginationUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Service
public class StockService {

    private final Logger logger = LoggerFactory.getLogger(StockService.class);

    @Inject
    private StockRepository stockRepository;

    @Inject
    private StockDetailsRepository stockDetailsRepository;

    @Inject
    private StockIndicatorsRepository stockIndicatorsRepository;

    @Inject
    private UserService userService;

    @Inject
    private UserRepository userRepository;

    @Cacheable(value = CacheConfiguration.ALL_STOCKS_FETCH_INDICATORS_CACHE)
    public List<StockWithStockIndicatorsDTO> getAllStocksFetchStockIndicators() {
        List<Stock> stocks = stockRepository.findAllFetchIndicators();
        ModelMapper modelMapper = new ModelMapper();
        Type dtoType = new TypeToken<List<StockWithStockIndicatorsDTO>>() {
        }.getType();
        return modelMapper.map(stocks, dtoType);
    }

    @Cacheable(value = CacheConfiguration.TRENDING_STOCKS_CACHE)
    public ResponseEntity<List<StockIndicatorsWithStocksDTO>> getTrendingStocks(TrendDirection trendDirection, int days, int offset, int limit) throws URISyntaxException {
        LocalDate date = stockDetailsRepository.findTopByOrderByDateDesc().getDate();
        switch (trendDirection) {
            case UP:
                switch (days) {
                    case 10:
                        Page<StockIndicators> stockIn10DaysTrendUp = stockIndicatorsRepository.findWithStocksIndicators10DaysTrendUp(PaginationUtil.generatePageRequest(offset, limit), date);
                        HttpHeaders headers10DaysTrendUp = PaginationUtil.generatePaginationHttpHeaders(stockIn10DaysTrendUp, "/api/stocks/trends/up/days", offset, limit);
                        return new ResponseEntity<List<StockIndicatorsWithStocksDTO>>(getStockWithStockIndicatorsDTOs(stockIn10DaysTrendUp.getContent()), headers10DaysTrendUp, HttpStatus.OK);
                    case 30:
                        Page<StockIndicators> stockIn30DaysTrendUp = stockIndicatorsRepository.findWithStocksIndicators30DaysTrendUp(PaginationUtil.generatePageRequest(offset, limit), date);
                        HttpHeaders headers30DaysTrendUp = PaginationUtil.generatePaginationHttpHeaders(stockIn30DaysTrendUp, "/api/stocks/trends/up/days", offset, limit);
                        return new ResponseEntity<List<StockIndicatorsWithStocksDTO>>(getStockWithStockIndicatorsDTOs(stockIn30DaysTrendUp.getContent()), headers30DaysTrendUp, HttpStatus.OK);
                    case 60:
                        Page<StockIndicators> stockIn60DaysTrendUp = stockIndicatorsRepository.findWithStocksIndicators60DaysTrendUp(PaginationUtil.generatePageRequest(offset, limit), date);
                        HttpHeaders headers60DaysTrendUp = PaginationUtil.generatePaginationHttpHeaders(stockIn60DaysTrendUp, "/api/stocks/trends/up/days", offset, limit);
                        return new ResponseEntity<List<StockIndicatorsWithStocksDTO>>(getStockWithStockIndicatorsDTOs(stockIn60DaysTrendUp.getContent()), headers60DaysTrendUp, HttpStatus.OK);
                    case 90:
                        Page<StockIndicators> stockIn90DaysTrendUp = stockIndicatorsRepository.findWithStocksIndicators90DaysTrendUp(PaginationUtil.generatePageRequest(offset, limit), date);
                        HttpHeaders headers90DaysTrendUp = PaginationUtil.generatePaginationHttpHeaders(stockIn90DaysTrendUp, "/api/stocks/trends/up/days", offset, limit);
                        return new ResponseEntity<List<StockIndicatorsWithStocksDTO>>(getStockWithStockIndicatorsDTOs(stockIn90DaysTrendUp.getContent()), headers90DaysTrendUp, HttpStatus.OK);
                    default:
                        break;
                }
            case DOWN:
                switch (days) {
                    case 10:
                        Page<StockIndicators> stockIn10DaysTrendDown = stockIndicatorsRepository.findWithStocksIndicators10DaysTrendDown(PaginationUtil.generatePageRequest(offset, limit), date);
                        HttpHeaders headers10DaysTrendDown = PaginationUtil.generatePaginationHttpHeaders(stockIn10DaysTrendDown, "/api/stocks/trends/down/days", offset, limit);
                        return new ResponseEntity<List<StockIndicatorsWithStocksDTO>>(getStockWithStockIndicatorsDTOs(stockIn10DaysTrendDown.getContent()), headers10DaysTrendDown, HttpStatus.OK);
                    case 30:
                        Page<StockIndicators> stockIn30DaysTrendDown = stockIndicatorsRepository.findWithStocksIndicators30DaysTrendDown(PaginationUtil.generatePageRequest(offset, limit), date);
                        HttpHeaders headers30DaysTrendDown = PaginationUtil.generatePaginationHttpHeaders(stockIn30DaysTrendDown, "/api/stocks/trends/down/days", offset, limit);
                        return new ResponseEntity<List<StockIndicatorsWithStocksDTO>>(getStockWithStockIndicatorsDTOs(stockIn30DaysTrendDown.getContent()), headers30DaysTrendDown, HttpStatus.OK);
                    case 60:
                        Page<StockIndicators> stockIn60DaysTrendDown = stockIndicatorsRepository.findWithStocksIndicators60DaysTrendDown(PaginationUtil.generatePageRequest(offset, limit), date);
                        HttpHeaders headers60DaysTrendDown = PaginationUtil.generatePaginationHttpHeaders(stockIn60DaysTrendDown, "/api/stocks/trends/down/days", offset, limit);
                        return new ResponseEntity<List<StockIndicatorsWithStocksDTO>>(getStockWithStockIndicatorsDTOs(stockIn60DaysTrendDown.getContent()), headers60DaysTrendDown, HttpStatus.OK);
                    case 90:
                        Page<StockIndicators> stockIn90DaysTrendDown = stockIndicatorsRepository.findWithStocksIndicators90DaysTrendDown(PaginationUtil.generatePageRequest(offset, limit), date);
                        HttpHeaders headers90DaysTrendDown = PaginationUtil.generatePaginationHttpHeaders(stockIn90DaysTrendDown, "/api/stocks/trends/down/days", offset, limit);
                        return new ResponseEntity<List<StockIndicatorsWithStocksDTO>>(getStockWithStockIndicatorsDTOs(stockIn90DaysTrendDown.getContent()), headers90DaysTrendDown, HttpStatus.OK);
                }
        }
        return new ResponseEntity<List<StockIndicatorsWithStocksDTO>>(HttpStatus.NO_CONTENT);
    }

    private List<StockIndicatorsWithStocksDTO> getStockWithStockIndicatorsDTOs(List<?> stocks) {
        ModelMapper modelMapper = new ModelMapper();
        Type dtoType = new TypeToken<List<StockIndicatorsWithStocksDTO>>() {
        }.getType();
        return modelMapper.map(stocks, dtoType);
    }

    @Transactional
    public ResponseEntity<Void> followStock(String stockId) throws URISyntaxException {
        User user = userService.getUserWithAuthorities();
        userRepository.createAssociationWithStock(user.getId(), stockId);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Void> stopFollowStock(String stockId) throws URISyntaxException {
        User user = userService.getUserWithAuthorities();
        userRepository.deleteAssociationWithStock(user.getId(), stockId);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<List<StockDTO>> getStocksFollowedByUser() {
        User user = userService.getUserWithAuthorities();
        List<Stock> followedStocks = stockRepository.findStocksByUserId(user.getId());
        return new ResponseEntity<List<StockDTO>>(getStockDTOs(followedStocks), HttpStatus.OK);
    }

    private List<StockDTO> getStockDTOs(Collection<Stock> stocks) {
        ModelMapper modelMapper = new ModelMapper();
        Type dto = new TypeToken<List<StockDTO>>() {
        }.getType();
        return modelMapper.map(stocks, dto);
    }

}
