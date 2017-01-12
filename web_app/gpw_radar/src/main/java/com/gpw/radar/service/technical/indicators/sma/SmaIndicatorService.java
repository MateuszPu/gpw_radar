package com.gpw.radar.service.technical.indicators.sma;

import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.repository.stock.StockDetailsRepository;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.service.stock.StockService;
import com.gpw.radar.service.technical.indicators.TickAdapter;
import com.gpw.radar.web.rest.dto.stock.StockWithStockIndicatorsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pl.technical.analysis.Tickable;
import pl.technical.analysis.indicators.trackers.sma.PriceCrossSMA;
import pl.technical.analysis.indicators.trackers.sma.SMACrossover;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SmaIndicatorService {

    private final StockDetailsRepository stockDetailsRepository;
    private final StockRepository stockRepository;
    private final StockService stockService;

    @Autowired
    public SmaIndicatorService(final StockDetailsRepository stockDetailsRepository, final StockRepository stockRepository, final StockService stockService) {
        this.stockDetailsRepository = stockDetailsRepository;
        this.stockRepository = stockRepository;
        this.stockService = stockService;
    }

    public List<StockWithStockIndicatorsDTO> getStocksSmaCrossover(CrossDirection direction, int fasterSma, int slowerSma) {
        Set<String> tickers = getStocksSmaCrossover(CrossType.SMA_CROSSOVER, fasterSma, slowerSma).get(direction);
        List<StockWithStockIndicatorsDTO> allStocksFetchStockIndicators = stockService.getAllStocksFetchStockIndicators();
        return allStocksFetchStockIndicators.stream().filter(st -> tickers.contains(st.getStockTicker())).collect(Collectors.toList());
    }

    public List<StockWithStockIndicatorsDTO> getStocksPriceCrossSma(CrossDirection direction, int sma) {
        Set<String> tickers = getStocksSmaCrossover(CrossType.PRICE_CROSS_SMA, sma).get(direction);
        return stockService.getAllStocksFetchStockIndicators().stream().filter(st -> tickers.contains(st.getStockTicker())).collect(Collectors.toList());
    }

    private Map<CrossDirection, Set<String>> getStocksSmaCrossover(CrossType type, int sma) {
        return getStocksSmaCrossover(type, sma, sma);
    }

    private Map<CrossDirection, Set<String>> getStocksSmaCrossover(CrossType type, int fasterSma, int slowerSma) {
        Map<CrossDirection, Set<String>> result = new HashMap<>();
        result.put(CrossDirection.FROM_ABOVE, new HashSet());
        result.put(CrossDirection.FROM_BELOW, new HashSet());

        for (String ticker : stockRepository.findAllTickers()) {
            List<StockDetails> stockDetails = stockDetailsRepository.findByStockTickerOrderByDateDesc(ticker, new PageRequest(0, 90)).getContent();
            List<Tickable> ticks = stockDetails.stream().map(e -> new TickAdapter(e)).collect(Collectors.toList());
            if (ticks.size() < slowerSma) {
                continue;
            }
            Crossable crossable = getCrossable(type, ticks, fasterSma, slowerSma);

            if (crossable.crossFromAbove()) {
                result.get(CrossDirection.FROM_ABOVE).add(ticker);
            }
            if (crossable.crossFromBelow()) {
                result.get(CrossDirection.FROM_BELOW).add(ticker);
            }
        }
        return result;
    }

    private Crossable getCrossable(CrossType type, List<Tickable> ticks, int fasterSma, int slowerSma) {
        switch (type) {
            case PRICE_CROSS_SMA:
                return new PriceCrossSmaAdapter(new PriceCrossSMA(ticks, fasterSma));
            case SMA_CROSSOVER:
                return new SmaCrossoverAdapter(new SMACrossover(ticks, fasterSma, slowerSma));
            default:
                throw new IllegalArgumentException(String.format("Provided type %s not handle in switch case", type));
        }
    }
}
