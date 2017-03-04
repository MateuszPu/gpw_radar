package com.gpw.radar.web.rest.stock;

import com.gpw.radar.dao.stockdetails.StockDetailsDAO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.io.IOException;
import java.time.LocalDate;

/**
 * REST controller for managing stock details.
 */
@RestController
@RequestMapping("/api")
public class StockDetailsResource {

    @Inject
    @Qualifier("stockDetailsElasticSearchDAO")
    private StockDetailsDAO stockDetailsDAO;

    @RequestMapping(value = "/get/top/by/date", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LocalDate> getTopStockDetailsByDate() throws IOException {
        return new ResponseEntity<>(stockDetailsDAO.findTopDate(), HttpStatus.OK);
    }
}
