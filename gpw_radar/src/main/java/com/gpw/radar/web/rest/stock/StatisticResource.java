package com.gpw.radar.web.rest.stock;

import com.gpw.radar.domain.stock.StockStatistic;
import com.gpw.radar.security.AuthoritiesConstants;
import com.gpw.radar.service.correlation.CorrelationService;
import com.gpw.radar.service.correlation.CorrelationType;
import com.gpw.radar.service.stock.StatisticService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.util.EnumSet;
import java.util.List;
import java.util.TreeSet;

/**
 * REST controller for managing statistics.
 */
@RestController
@RequestMapping("/api/statistic")
public class StatisticResource {

    @Inject
    private CorrelationService correlationService;

    @Inject
    private StatisticService statisticService;

    @RequestMapping(value = "/stock/correlation", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<TreeSet<StockStatistic>> getCorrelationForSelectedTicker(@RequestParam(value = "correlation_type", required = true) CorrelationType correlationType, @RequestParam(value = "ticker", required = true) String ticker, @RequestParam(value = "period", required = true) int period) {
        return correlationService.computeCorrelation(ticker, period, correlationType);
    }

    @RequestMapping(value = "/stock/correlation/step", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<Integer> getStep() {
        return new ResponseEntity<Integer>(correlationService.getStep(), HttpStatus.OK);
    }

    @RequestMapping(value = "/stocks/up", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> getStocksUp() {
        return statisticService.countStocksUp();
    }

    @RequestMapping(value = "/stocks/down", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> getStocksDown() {
        return statisticService.countStocksDown();
    }

    @RequestMapping(value = "/stocks/no/change", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> getStocksNoChange() {
        return statisticService.countStocksNoChange();
    }

    @RequestMapping(value = "/five/most/followed/stocks", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StockStatistic>> getFiveMostFollowedStocks() {
        return statisticService.getFiveMostFollowedStocks();
    }

    @RequestMapping(value = "all/type/correlation", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EnumSet<CorrelationType>> getAllCorrelationTypes() {
        return new ResponseEntity<EnumSet<CorrelationType>>(EnumSet.allOf(CorrelationType.class), HttpStatus.OK);
    }

}
