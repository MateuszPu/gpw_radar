package com.gpw.radar.service;

import com.gpw.radar.Application;
import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.repository.stock.StockDetailsRepository;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.service.database.StockDetailsTextFileParserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
public class StockDetailsTextFileParserServiceTest {

    @Inject
    private StockDetailsTextFileParserService stockDetailsTextFileParserService;

    @Inject
    private StockRepository stockRepository;

    @Inject
    private StockDetailsRepository stockDetailsRepository;

    private List<StockTicker> stockTickerList = new ArrayList<>();
    private Stock stock = new Stock();

    @Before
    public void prepareVariables() {
        stockTickerList.add(StockTicker.cdr);
        stock.setTicker(StockTicker.cdr);
        stock.setStockName("CDR");
        stock.setStockShortName("CDR");
        stockRepository.save(stock);
    }

    @Test
    public void parseStockDetailsFromFile() {
        Set<StockDetails> list = stockDetailsTextFileParserService.parseStockDetailsByStockFromTxtFile(stock);
        assertThat(!list.isEmpty());
    }
}
