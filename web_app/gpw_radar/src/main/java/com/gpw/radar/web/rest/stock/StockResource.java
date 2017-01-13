package com.gpw.radar.web.rest.stock;

import com.gpw.radar.domain.enumeration.TrendDirection;
import com.gpw.radar.elasticsearch.service.stockdetails.StockDetailsDAO;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.security.AuthoritiesConstants;
import com.gpw.radar.service.UserService;
import com.gpw.radar.service.stock.StockService;
import com.gpw.radar.web.rest.dto.stock.StockIndicatorsWithStocksDTO;
import com.gpw.radar.web.rest.dto.stock.StockWithStockIndicatorsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * REST controller for managing stocks
 */
@RestController
@RequestMapping("/api")
public class StockResource {

    private final StockService stockService;
    private final UserService userService;
    private final StockRepository stockRepository;
    private final StockDetailsDAO stockDetailsDAO;

    @Autowired
    public StockResource(StockService stockService, UserService userService, StockRepository stockRepository,
                         @Qualifier("stockDetailsElasticSearchDAO")StockDetailsDAO stockDetailsDAO) {
        this.stockService = stockService;
        this.userService = userService;
        this.stockRepository = stockRepository;
        this.stockDetailsDAO = stockDetailsDAO;
    }

    @RequestMapping(value = "/stocks/get/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<List<StockWithStockIndicatorsDTO>> getAllWithOutPagination() throws URISyntaxException {
        return new ResponseEntity<>(stockService.getAllStocksFetchStockIndicators(), HttpStatus.OK);
    }

    @RequestMapping(value = "/stocks/trends/{direction}/days", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<List<StockIndicatorsWithStocksDTO>> getStocksTrend(@PathVariable TrendDirection direction, @RequestParam int days, @RequestParam(value = "page") Integer offset,
                                                                             @RequestParam(value = "per_page") Integer limit) throws URISyntaxException {
        LocalDate topDate = stockDetailsDAO.findTopDate();
        return stockService.getTrendingStocks(topDate, direction, days, offset, limit);
    }

    @RequestMapping(value = "/stock/follow", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<Void> followStock(@RequestBody String id) throws URISyntaxException {
        return userService.followStock(id);
    }

    @RequestMapping(value = "/stock/stop/follow", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<Void> stopFollowStock(@RequestBody String id) throws URISyntaxException {
        return userService.stopFollowStock(id);
    }

    @RequestMapping(value = "/get/tickers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<String>> getAllTickers() {
        return new ResponseEntity<>(stockRepository.findAllTickers(), HttpStatus.OK);
    }
}
