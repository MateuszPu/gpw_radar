package com.gpw.radar.web.rest.technical.indicators.sma;

import com.gpw.radar.security.AuthoritiesConstants;
import com.gpw.radar.service.technical.indicators.sma.CrossDirection;
import com.gpw.radar.service.technical.indicators.sma.SmaIndicatorService;
import com.gpw.radar.web.rest.dto.stock.StockWithStockIndicatorsDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.util.List;

@RestController
@RequestMapping("/api/technical/indicators")
@RolesAllowed(AuthoritiesConstants.USER)
public class TechnicalIndicatorsResource {

    @Inject
    private SmaIndicatorService smaIndicatorService;

    @RequestMapping(value = "/sma/crossover", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StockWithStockIndicatorsDTO>> getCorrelationForSelectedTicker(@RequestParam(value = "direction", required = true) CrossDirection crossDirection, @RequestParam(value = "fasterSma", required = true) int fasterSma, @RequestParam(value = "slowerSma", required = true) int slowerSma) {
        List<StockWithStockIndicatorsDTO> stocksSmaCrossover = smaIndicatorService.getStocksSmaCrossover(crossDirection, fasterSma, slowerSma);
        return new ResponseEntity<>(stocksSmaCrossover, HttpStatus.OK);
    }

    @RequestMapping(value = "/price/cross/sma", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StockWithStockIndicatorsDTO>> getCorrelationForSelectedTicker(@RequestParam(value = "direction", required = true) CrossDirection crossDirection, @RequestParam(value = "sma", required = true) int sma) {
        List<StockWithStockIndicatorsDTO> stocksPriceCrossSma = smaIndicatorService.getStocksPriceCrossSma(crossDirection, sma);
        return new ResponseEntity<>(stocksPriceCrossSma, HttpStatus.OK);
    }
}
