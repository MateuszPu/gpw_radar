package com.gpw.radar.service.stock;

import com.gpw.radar.config.CacheConfiguration;
import com.gpw.radar.domain.enumeration.TrendDirection;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockIndicators;
import com.gpw.radar.elasticsearch.domain.stockdetails.StockDetails;
import com.gpw.radar.repository.stock.StockIndicatorsRepository;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.service.parser.web.UrlStreamsGetterService;
import com.gpw.radar.service.parser.web.stock.StockDataDetailsWebParser;
import com.gpw.radar.web.rest.dto.stock.StockDTO;
import com.gpw.radar.web.rest.dto.stock.StockIndicatorsWithStocksDTO;
import com.gpw.radar.web.rest.dto.stock.StockWithStockIndicatorsDTO;
import com.gpw.radar.web.rest.util.PaginationUtil;
import org.jsoup.nodes.Document;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class StockService {

    private final Logger logger = LoggerFactory.getLogger(StockService.class);

    private final StockRepository stockRepository;
    private final StockIndicatorsRepository stockIndicatorsRepository;
    private final StockDataDetailsWebParser detailsParser;
    private final UrlStreamsGetterService urlStreamsGetterService;

    @Autowired
    public StockService(StockRepository stockRepository,
                        @Qualifier("stooqDataParserService") StockDataDetailsWebParser detailsParser,
                        UrlStreamsGetterService urlStreamsGetterService,
                        StockIndicatorsRepository stockIndicatorsRepository) {
        this.stockRepository = stockRepository;
        this.detailsParser = detailsParser;
        this.urlStreamsGetterService = urlStreamsGetterService;
        this.stockIndicatorsRepository = stockIndicatorsRepository;
    }

    @Cacheable(value = CacheConfiguration.ALL_STOCKS_FETCH_INDICATORS_CACHE)
    public List<StockWithStockIndicatorsDTO> getAllStocksFetchStockIndicators() {
        List<Stock> stocks = stockRepository.findAllFetchIndicators();
        ModelMapper modelMapper = new ModelMapper();
        Type dtoType = new TypeToken<List<StockWithStockIndicatorsDTO>>() {
        }.getType();
        return modelMapper.map(stocks, dtoType);
    }

    @Cacheable(value = CacheConfiguration.TRENDING_STOCKS_CACHE)
    public ResponseEntity<List<StockIndicatorsWithStocksDTO>> getTrendingStocks(LocalDate date, TrendDirection trendDirection, int days, int offset, int limit) throws URISyntaxException {
        switch (trendDirection) {
            case UP:
                switch (days) {
                    case 10:
                        Page<StockIndicators> stockIn10DaysTrendUp = stockIndicatorsRepository.findWithStocksIndicators10DaysTrendUp(PaginationUtil.generatePageRequest(offset, limit), date);
                        HttpHeaders headers10DaysTrendUp = PaginationUtil.generatePaginationHttpHeaders(stockIn10DaysTrendUp, "/api/stocks/trends/up/days", offset, limit);
                        return new ResponseEntity<>(getStockWithStockIndicatorsDTOs(stockIn10DaysTrendUp.getContent()), headers10DaysTrendUp, HttpStatus.OK);
                    case 30:
                        Page<StockIndicators> stockIn30DaysTrendUp = stockIndicatorsRepository.findWithStocksIndicators30DaysTrendUp(PaginationUtil.generatePageRequest(offset, limit), date);
                        HttpHeaders headers30DaysTrendUp = PaginationUtil.generatePaginationHttpHeaders(stockIn30DaysTrendUp, "/api/stocks/trends/up/days", offset, limit);
                        return new ResponseEntity<>(getStockWithStockIndicatorsDTOs(stockIn30DaysTrendUp.getContent()), headers30DaysTrendUp, HttpStatus.OK);
                    case 60:
                        Page<StockIndicators> stockIn60DaysTrendUp = stockIndicatorsRepository.findWithStocksIndicators60DaysTrendUp(PaginationUtil.generatePageRequest(offset, limit), date);
                        HttpHeaders headers60DaysTrendUp = PaginationUtil.generatePaginationHttpHeaders(stockIn60DaysTrendUp, "/api/stocks/trends/up/days", offset, limit);
                        return new ResponseEntity<>(getStockWithStockIndicatorsDTOs(stockIn60DaysTrendUp.getContent()), headers60DaysTrendUp, HttpStatus.OK);
                    case 90:
                        Page<StockIndicators> stockIn90DaysTrendUp = stockIndicatorsRepository.findWithStocksIndicators90DaysTrendUp(PaginationUtil.generatePageRequest(offset, limit), date);
                        HttpHeaders headers90DaysTrendUp = PaginationUtil.generatePaginationHttpHeaders(stockIn90DaysTrendUp, "/api/stocks/trends/up/days", offset, limit);
                        return new ResponseEntity<>(getStockWithStockIndicatorsDTOs(stockIn90DaysTrendUp.getContent()), headers90DaysTrendUp, HttpStatus.OK);
                }
            case DOWN:
                switch (days) {
                    case 10:
                        Page<StockIndicators> stockIn10DaysTrendDown = stockIndicatorsRepository.findWithStocksIndicators10DaysTrendDown(PaginationUtil.generatePageRequest(offset, limit), date);
                        HttpHeaders headers10DaysTrendDown = PaginationUtil.generatePaginationHttpHeaders(stockIn10DaysTrendDown, "/api/stocks/trends/down/days", offset, limit);
                        return new ResponseEntity<>(getStockWithStockIndicatorsDTOs(stockIn10DaysTrendDown.getContent()), headers10DaysTrendDown, HttpStatus.OK);
                    case 30:
                        Page<StockIndicators> stockIn30DaysTrendDown = stockIndicatorsRepository.findWithStocksIndicators30DaysTrendDown(PaginationUtil.generatePageRequest(offset, limit), date);
                        HttpHeaders headers30DaysTrendDown = PaginationUtil.generatePaginationHttpHeaders(stockIn30DaysTrendDown, "/api/stocks/trends/down/days", offset, limit);
                        return new ResponseEntity<>(getStockWithStockIndicatorsDTOs(stockIn30DaysTrendDown.getContent()), headers30DaysTrendDown, HttpStatus.OK);
                    case 60:
                        Page<StockIndicators> stockIn60DaysTrendDown = stockIndicatorsRepository.findWithStocksIndicators60DaysTrendDown(PaginationUtil.generatePageRequest(offset, limit), date);
                        HttpHeaders headers60DaysTrendDown = PaginationUtil.generatePaginationHttpHeaders(stockIn60DaysTrendDown, "/api/stocks/trends/down/days", offset, limit);
                        return new ResponseEntity<>(getStockWithStockIndicatorsDTOs(stockIn60DaysTrendDown.getContent()), headers60DaysTrendDown, HttpStatus.OK);
                    case 90:
                        Page<StockIndicators> stockIn90DaysTrendDown = stockIndicatorsRepository.findWithStocksIndicators90DaysTrendDown(PaginationUtil.generatePageRequest(offset, limit), date);
                        HttpHeaders headers90DaysTrendDown = PaginationUtil.generatePaginationHttpHeaders(stockIn90DaysTrendDown, "/api/stocks/trends/down/days", offset, limit);
                        return new ResponseEntity<>(getStockWithStockIndicatorsDTOs(stockIn90DaysTrendDown.getContent()), headers90DaysTrendDown, HttpStatus.OK);
                }
            default:
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    public StockDetails addMissingData(StockDetails stockDetails) {
        String ticker = stockDetails.getStock().getTicker();
        Stock stock = findStockByTicker(ticker);
        stockDetails.setStockWith(stock.getTicker(), stock.getStockName(), stock.getStockShortName());
        return stockDetails;
    }

    private Stock findStockByTicker(String ticker) {
        Stock byTicker = stockRepository.findByTicker(ticker);
        Optional<Stock> stock = byTicker == null ? Optional.empty() : Optional.of(byTicker);
        if (!stock.isPresent()) {
            Document doc = urlStreamsGetterService.getDocFromUrl("http://stooq.pl/q/?s=" + ticker);
            stock = Optional.of(createStock(ticker, doc));
            stockRepository.save(stock.get());
        }
        return stock.get();
    }

    private Stock createStock(String ticker, Document doc) {
        Stock stock = new Stock();
        stock.setTicker(ticker.toLowerCase());
        String stockName = detailsParser.getStockNameFromWeb(doc);
        String stockShortName = detailsParser.getStockShortNameFromWeb(doc);
        stock.setStockName(stockName);
        stock.setStockShortName(stockShortName);
        return stock;
    }

    public ResponseEntity<List<StockDTO>> getStocksFollowedByUser(String login) {
        Set<Stock> stocksFollowedByUser = stockRepository.findFollowedByUserLogin(login);
        return new ResponseEntity<>(getStockDTOs(stocksFollowedByUser), HttpStatus.OK);
    }

    private List<StockIndicatorsWithStocksDTO> getStockWithStockIndicatorsDTOs(List<?> stocks) {
        ModelMapper modelMapper = new ModelMapper();
        Type dtoType = new TypeToken<List<StockIndicatorsWithStocksDTO>>() {
        }.getType();
        return modelMapper.map(stocks, dtoType);
    }

    private List<StockDTO> getStockDTOs(Collection<Stock> stocks) {
        ModelMapper modelMapper = new ModelMapper();
        Type dto = new TypeToken<List<StockDTO>>() {
        }.getType();
        return modelMapper.map(stocks, dto);
    }
}
