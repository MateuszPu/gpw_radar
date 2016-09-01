package com.gpw.radar.web.rest.stock;

import com.gpw.radar.domain.stock.TimeStockFiveMinuteDetails;
import com.gpw.radar.service.stock.StockFiveMinutesDetailsService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.io.IOException;

/**
 * REST controller for managing stock five minutes details.
 */
@RestController
@RequestMapping("/api")
public class StockFiveMinutesDetailsResource {

    @Inject
    private StockFiveMinutesDetailsService stockFiveMinutesDetailsService;

    @RequestMapping(value = "/5/minutes/stocks/most/active", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TimeStockFiveMinuteDetails> getTopStockDetailsByDate(@RequestParam(value = "time") Long time) throws IOException {
        return stockFiveMinutesDetailsService.findTodaysStockFiveMinutesDetails(time);
    }
}
