package com.gpw.radar.service.stock;

import com.gpw.radar.config.CacheConfiguration;
import com.gpw.radar.domain.enumeration.TrendDirection;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockIndicators;
import com.gpw.radar.elasticsearch.stockdetails.StockDetails;
import com.gpw.radar.repository.stock.StockIndicatorsRepository;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.service.parser.web.UrlStreamsGetterService;
import com.gpw.radar.service.parser.web.stock.StockDataDetailsWebParser;
import com.gpw.radar.web.rest.dto.stock.StockDTO;
import com.gpw.radar.web.rest.dto.stock.StockIndicatorsWithStocksDTO;
import com.gpw.radar.web.rest.dto.stock.StockWithStockIndicatorsDTO;
import com.gpw.radar.web.rest.util.PaginationUtil;
import org.jsoup.nodes.Document;
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

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class StockService {

    private final Logger logger = LoggerFactory.getLogger(StockService.class);

    private final StockRepository stockRepository;
    private final StockIndicatorsRepository stockIndicatorsRepository;
    private final StockDataDetailsWebParser detailsParser;
    private final UrlStreamsGetterService urlStreamsGetterService;
    private final StockMapperDtoFacade stockMapperDtoFacade;

    @Autowired
    public StockService(StockRepository stockRepository,
                        @Qualifier("stooqDataParserService") StockDataDetailsWebParser detailsParser,
                        UrlStreamsGetterService urlStreamsGetterService,
                        StockIndicatorsRepository stockIndicatorsRepository,
                        StockMapperDtoFacade stockMapperDtoFacade) {
        this.stockRepository = stockRepository;
        this.detailsParser = detailsParser;
        this.urlStreamsGetterService = urlStreamsGetterService;
        this.stockIndicatorsRepository = stockIndicatorsRepository;
        this.stockMapperDtoFacade = stockMapperDtoFacade;
    }

    @Cacheable(value = CacheConfiguration.ALL_STOCKS_FETCH_INDICATORS_CACHE)
    public List<StockWithStockIndicatorsDTO> getAllStocksFetchStockIndicators() {
        List<Stock> stocks = stockRepository.findAllFetchIndicators();
        return stockMapperDtoFacade.mapToStockWithIndicatorsDto(stocks);
    }

    @Cacheable(value = CacheConfiguration.TRENDING_STOCKS_CACHE)
    public ResponseEntity<List<StockIndicatorsWithStocksDTO>> getTrendingStocks(LocalDate date, TrendDirection trendDirection, int days, int offset, int limit) throws URISyntaxException {
        List<StockIndicatorsWithStocksDTO> result = null;
        HttpHeaders httpHeaders = null;
        switch (trendDirection) {
            case UP:
                switch (days) {
                    case 10:
                        Page<StockIndicators> stockIn10DaysTrendUp = stockIndicatorsRepository.findWithStocksIndicators10DaysTrendUp(PaginationUtil.generatePageRequest(offset, limit), date);
                        httpHeaders = PaginationUtil.generatePaginationHttpHeaders(stockIn10DaysTrendUp, "/api/stocks/trends/up/days", offset, limit);
                        result = stockMapperDtoFacade.mapToStockIndiactorsDto(stockIn10DaysTrendUp.getContent());
                        break;
                    case 30:
                        Page<StockIndicators> stockIn30DaysTrendUp = stockIndicatorsRepository.findWithStocksIndicators30DaysTrendUp(PaginationUtil.generatePageRequest(offset, limit), date);
                        httpHeaders = PaginationUtil.generatePaginationHttpHeaders(stockIn30DaysTrendUp, "/api/stocks/trends/up/days", offset, limit);
                        result = stockMapperDtoFacade.mapToStockIndiactorsDto(stockIn30DaysTrendUp.getContent());
                        break;
                    case 60:
                        Page<StockIndicators> stockIn60DaysTrendUp = stockIndicatorsRepository.findWithStocksIndicators60DaysTrendUp(PaginationUtil.generatePageRequest(offset, limit), date);
                        httpHeaders = PaginationUtil.generatePaginationHttpHeaders(stockIn60DaysTrendUp, "/api/stocks/trends/up/days", offset, limit);
                        result = stockMapperDtoFacade.mapToStockIndiactorsDto(stockIn60DaysTrendUp.getContent());
                        break;
                    case 90:
                        Page<StockIndicators> stockIn90DaysTrendUp = stockIndicatorsRepository.findWithStocksIndicators90DaysTrendUp(PaginationUtil.generatePageRequest(offset, limit), date);
                        httpHeaders = PaginationUtil.generatePaginationHttpHeaders(stockIn90DaysTrendUp, "/api/stocks/trends/up/days", offset, limit);
                        result = stockMapperDtoFacade.mapToStockIndiactorsDto(stockIn90DaysTrendUp.getContent());
                        break;
                }
                break;
            case DOWN:
                switch (days) {
                    case 10:
                        Page<StockIndicators> stockIn10DaysTrendDown = stockIndicatorsRepository.findWithStocksIndicators10DaysTrendDown(PaginationUtil.generatePageRequest(offset, limit), date);
                        httpHeaders = PaginationUtil.generatePaginationHttpHeaders(stockIn10DaysTrendDown, "/api/stocks/trends/down/days", offset, limit);
                        result = stockMapperDtoFacade.mapToStockIndiactorsDto(stockIn10DaysTrendDown.getContent());
                        break;
                    case 30:
                        Page<StockIndicators> stockIn30DaysTrendDown = stockIndicatorsRepository.findWithStocksIndicators30DaysTrendDown(PaginationUtil.generatePageRequest(offset, limit), date);
                        httpHeaders = PaginationUtil.generatePaginationHttpHeaders(stockIn30DaysTrendDown, "/api/stocks/trends/down/days", offset, limit);
                        result = stockMapperDtoFacade.mapToStockIndiactorsDto(stockIn30DaysTrendDown.getContent());
                        break;
                    case 60:
                        Page<StockIndicators> stockIn60DaysTrendDown = stockIndicatorsRepository.findWithStocksIndicators60DaysTrendDown(PaginationUtil.generatePageRequest(offset, limit), date);
                        httpHeaders = PaginationUtil.generatePaginationHttpHeaders(stockIn60DaysTrendDown, "/api/stocks/trends/down/days", offset, limit);
                        result = stockMapperDtoFacade.mapToStockIndiactorsDto(stockIn60DaysTrendDown.getContent());
                        break;
                    case 90:
                        Page<StockIndicators> stockIn90DaysTrendDown = stockIndicatorsRepository.findWithStocksIndicators90DaysTrendDown(PaginationUtil.generatePageRequest(offset, limit), date);
                        httpHeaders = PaginationUtil.generatePaginationHttpHeaders(stockIn90DaysTrendDown, "/api/stocks/trends/down/days", offset, limit);
                        result = stockMapperDtoFacade.mapToStockIndiactorsDto(stockIn90DaysTrendDown.getContent());
                        break;
                }
            default:
                break;
        }
        return new ResponseEntity<>(result, httpHeaders, HttpStatus.OK);
    }

    public StockDetails setNameAndShortNameOfStock(StockDetails stockDetails) {
        String ticker = stockDetails.getStock().getTicker();
        Stock stock = findStockByTicker(ticker);
        stockDetails.setStockWith(stock.getTicker(), stock.getStockName(), stock.getStockShortName());
        return stockDetails;
    }

    private Stock findStockByTicker(String ticker) {
        Stock stock = stockRepository.findByTicker(ticker);
        if (stock == null) {
            Document doc = urlStreamsGetterService.getDocFromUrl("http://stooq.pl/q/?s=" + ticker);
            stock = createStock(ticker, doc);
            stockRepository.save(stock);
        }
        return stock;
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
        List<StockDTO> stockDtos = stockMapperDtoFacade.mapToStocksDto(stocksFollowedByUser);
        return new ResponseEntity<>(stockDtos, HttpStatus.OK);
    }

}
