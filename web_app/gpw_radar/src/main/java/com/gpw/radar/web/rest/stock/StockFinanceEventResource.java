package com.gpw.radar.web.rest.stock;

import com.gpw.radar.security.AuthoritiesConstants;
import com.gpw.radar.service.stock.StockFinanceEventService;
import com.gpw.radar.web.rest.dto.stock.StockWithStockFinanceEventDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.net.URISyntaxException;
import java.util.List;

/**
 * REST controller for managing stock finance events.
 */
@RestController
@RequestMapping("/api")
public class StockFinanceEventResource {

    @Inject
    private StockFinanceEventService stockFinanceEventService;

    @RequestMapping(value = "/all/finance/event", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<List<StockWithStockFinanceEventDTO>> getAll() throws URISyntaxException {
        return stockFinanceEventService.getAllStockFinanceEvent();
    }
}
