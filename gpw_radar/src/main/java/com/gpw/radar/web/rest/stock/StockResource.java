package com.gpw.radar.web.rest.stock;

import com.gpw.radar.domain.enumeration.TrendDirection;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.security.AuthoritiesConstants;
import com.gpw.radar.service.stock.StockService;
import com.gpw.radar.web.rest.dto.stock.StockIndicatorsWithStocksDTO;
import com.gpw.radar.web.rest.dto.stock.StockWithStockIndicatorsDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

/**
 * REST controller for managing stocks
 */
@RestController
@RequestMapping("/api")
public class StockResource {

    @Inject
    private StockService stockService;

    @Inject
    private StockRepository stockRepository;

    @RequestMapping(value = "/stocks/get/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<List<StockWithStockIndicatorsDTO>> getAllWithOutPagination() throws URISyntaxException {
        return new ResponseEntity<List<StockWithStockIndicatorsDTO>>(stockService.getAllStocksFetchStockIndicators(), HttpStatus.OK);
    }

    @RequestMapping(value = "/stocks/trends/{direction}/days", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<List<StockIndicatorsWithStocksDTO>> getStocksTrend(@PathVariable TrendDirection direction, @RequestParam int days, @RequestParam(value = "page") Integer offset,
                                                                             @RequestParam(value = "per_page") Integer limit) throws URISyntaxException {
        return stockService.getTrendingStocks(direction, days, offset, limit);
    }

    @RequestMapping(value = "/stock/follow", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<Void> followStock(@RequestBody long id) throws URISyntaxException {
        return stockService.followStock(id);
    }

    @RequestMapping(value = "/stock/stop/follow", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<Void> stopFollowStock(@RequestBody long id) throws URISyntaxException {
        return stockService.stopFollowStock(id);
    }

    @RequestMapping(value = "/get/tickers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<String>> getAllTickers() {
        return new ResponseEntity<Set<String>>(stockRepository.findAllTickers(), HttpStatus.OK);
    }
}
