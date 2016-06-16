package com.gpw.radar.web.rest.technical.indicators;

import com.gpw.radar.domain.stock.StockStatistic;
import com.gpw.radar.security.AuthoritiesConstants;
import com.gpw.radar.service.correlation.CorrelationType;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.TreeSet;

@RestController
@RequestMapping("/api/technical/indicators")
@RolesAllowed(AuthoritiesConstants.USER)
public class TechnicalIndicatorsResource {

//    @RequestMapping(value = "/stock/correlation", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<TreeSet<StockStatistic>> getCorrelationForSelectedTicker(@RequestParam(value = "correlation_type", required = true) CorrelationType correlationType, @RequestParam(value = "ticker", required = true) String ticker, @RequestParam(value = "period", required = true) int period) {
//        return correlationService.computeCorrelation(ticker, period, correlationType);
//    }

}
