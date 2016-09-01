package com.gpw.radar.service.technicalIndicators;

import com.gpw.radar.Application;
import com.gpw.radar.config.CacheConfiguration;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.repository.stock.StockDetailsRepository;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.service.AbstractCleaner;
import com.gpw.radar.service.builders.StockBuilder;
import com.gpw.radar.service.parser.DateAndTimeParserService;
import com.gpw.radar.service.stock.StockService;
import com.gpw.radar.service.technical.indicators.sma.CrossDirection;
import com.gpw.radar.service.technical.indicators.sma.SmaIndicatorService;
import com.gpw.radar.web.rest.dto.stock.StockWithStockIndicatorsDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.StrictAssertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class SmaIndicatorServiceTest extends AbstractCleaner {

    @Inject
    private StockDetailsRepository stockDetailsRepository;

    @Inject
    private StockRepository stockRepository;

    @Inject
    private StockService stockService;

    @Inject
    private CacheManager cacheManager;

    private SmaIndicatorService smaIndicatorService;


    @Before
    @Transactional
    public void init() {
        stockDetailsRepository.deleteAll();
        stockRepository.deleteAll();
        cacheManager.getCache(CacheConfiguration.STOCK_TICKERS_CACHE).clear();
    }

    @Test
    public void testSmaCrossover() {
        prepareDb(LocalDate.of(2015, 11, 14), LocalDate.of(2015, 9, 23));
        smaIndicatorService = new SmaIndicatorService(stockDetailsRepository, stockRepository, stockService);

        List<StockWithStockIndicatorsDTO> resultFromAbove = smaIndicatorService.getStocksSmaCrossover(CrossDirection.FROM_ABOVE, 15, 30);
        assertThat(resultFromAbove.stream().anyMatch(st -> st.getStockTicker().equals("tpe"))).isEqualTo(true);
        assertThat(resultFromAbove.stream().anyMatch(st -> st.getStockTicker().equals("kgh"))).isEqualTo(false);
        List<StockWithStockIndicatorsDTO> resultFromBelow = smaIndicatorService.getStocksSmaCrossover(CrossDirection.FROM_BELOW, 15, 30);
        assertThat(resultFromBelow.stream().anyMatch(st -> st.getStockTicker().equals("pko"))).isEqualTo(true);
        assertThat(resultFromBelow.stream().anyMatch(st -> st.getStockTicker().equals("kgh"))).isEqualTo(false);
    }

    @Test
    public void priceCrossSmaTest() {
        prepareDb(LocalDate.of(2015, 12, 23), LocalDate.of(2015, 9, 23));

        smaIndicatorService = new SmaIndicatorService(stockDetailsRepository, stockRepository, stockService);

        List<StockWithStockIndicatorsDTO> resultFromAbove = smaIndicatorService.getStocksPriceCrossSma(CrossDirection.FROM_BELOW, 15);
        assertThat(resultFromAbove.stream().anyMatch(st -> st.getStockTicker().equals("tpe"))).isEqualTo(true);
        assertThat(resultFromAbove.stream().anyMatch(st -> st.getStockTicker().equals("kgh"))).isEqualTo(false);
        List<StockWithStockIndicatorsDTO> resultFromBelow = smaIndicatorService.getStocksPriceCrossSma(CrossDirection.FROM_ABOVE, 15);
        assertThat(resultFromBelow.stream().anyMatch(st -> st.getStockTicker().equals("pko"))).isEqualTo(true);
        assertThat(resultFromBelow.stream().anyMatch(st -> st.getStockTicker().equals("kgh"))).isEqualTo(false);

    }

    private void prepareDb(LocalDate dateOne, LocalDate dateTwo) {
        InputStream inputStreamOfStockDetails = getClass().getResourceAsStream("/stocks_data/daily/pl/wse_stocks/kgh.txt");
        Stock stockOne = StockBuilder.stockBuilder().ticker("kgh").build();
        List<StockDetails> stockDetailsOne = parseStockDetails(stockOne,  inputStreamOfStockDetails);
        stockDetailsRepository.save(stockDetailsOne);

        inputStreamOfStockDetails = getClass().getResourceAsStream("/stocks_data/daily/pl/wse_stocks/kgh.txt");
        Stock stockTwo = StockBuilder.stockBuilder().ticker("tpe").build();
        List<StockDetails> stockDetailsTwo = parseStockDetails(stockTwo,  inputStreamOfStockDetails);
        stockDetailsRepository.save(stockDetailsTwo.stream().filter(ti -> ti.getDate().isBefore(dateOne)).collect(Collectors.toList()));

        inputStreamOfStockDetails = getClass().getResourceAsStream("/stocks_data/daily/pl/wse_stocks/kgh.txt");
        Stock stockThree = StockBuilder.stockBuilder().ticker("pko").build();
        List<StockDetails> stockDetailsThree = parseStockDetails(stockThree,  inputStreamOfStockDetails);
        stockDetailsRepository.save(stockDetailsThree.stream().filter(ti -> ti.getDate().isBefore(dateTwo)).collect(Collectors.toList()));
    }

    private List<StockDetails> parseStockDetails(Stock stock, InputStream inputStreamOfStockDetails) {
        List<StockDetails> result = new ArrayList<>();
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStreamOfStockDetails));

        in.lines().forEach(ln -> result.add(parse(stock, ln)));

        return result;
    }

    private StockDetails parse(Stock stock, String ln) {
        DateAndTimeParserService parser = new DateAndTimeParserService(null);
        parser.init();

        String[] split = ln.split(",");
        StockDetails result = new StockDetails();
        result.setStock(stock);
        result.setDate(parser.parseLocalDateFromString(split[0]));
        result.setOpenPrice(new BigDecimal(split[1]));
        result.setMaxPrice(new BigDecimal(split[2]));
        result.setMinPrice(new BigDecimal(split[3]));
        result.setClosePrice(new BigDecimal(split[4]));
        result.setVolume(Long.valueOf(split[5]));
        return result;
    }
}
