package com.gpw.radar.web.rest;

import java.util.TreeSet;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gpw.radar.domain.StockStatistic;
import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.repository.StockRepository;
import com.gpw.radar.security.AuthoritiesConstants;
import com.gpw.radar.service.correlation.CorrelationService;
import com.gpw.radar.service.correlation.CorrelationType;

@RestController
@RequestMapping("/api/statistic")
public class StatisticResource {

    @Inject
    private StockRepository stockRepository;

    @Inject
    private CorrelationService statisticService;

    class CorrelationStatus {
        public int step;

        public CorrelationStatus(int step) {
            this.step = step;
        }
    }

    @RequestMapping(value = "/stock/correlation", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    public TreeSet<StockStatistic> getCorrelationForSelectedTicker(@RequestParam(value = "correlation_type", required = true) CorrelationType correlationType, @RequestParam(value = "ticker", required = true) StockTicker ticker, @RequestParam(value = "period", required = true) Integer period) {
        return statisticService.computeCorrelation(ticker, period, correlationType);
    }

    @RequestMapping(value = "/stock/correlation/step", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    public CorrelationStatus getStep(){
        return new CorrelationStatus(statisticService.getStep());
    }

    @RequestMapping(value = "/stocks/up", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public long getUpStocks() {
        return stockRepository.countUpStocks();
    }

    @RequestMapping(value = "/stocks/down", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public long getDownStocks() {
        return stockRepository.countDownStocks();
    }

    @RequestMapping(value = "/stocks/no/change", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public long getNoChangeStocks() {
        return stockRepository.countNoChangeStocks();
    }
}