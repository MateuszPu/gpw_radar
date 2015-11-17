package com.gpw.radar.service;

import com.gpw.radar.Application;
import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.service.database.StockDetailsTextFileParserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class StockDetailsTextFileParserServiceTest {

    @Inject
    private StockDetailsTextFileParserService stockDetailsTextFileParserService;

    private List<StockTicker> stockTickerList = new ArrayList<>();
    private Stock stock = new Stock();
    private InputStream st;

    @Before
    public void setup() {
        stockTickerList.add(StockTicker.cdr);
        stock.setTicker(StockTicker.cdr);
        stock.setStockName("CDR");
        stock.setStockShortName("CDR");
        String filePath = "/stocks_data/daily/pl/wse_stocks/" + stock.getTicker().name() + ".txt";
        st = getClass().getResourceAsStream(filePath);
    }

    @Test
    public void parseStockDetailsFromFileSizeTest() {
        Set<StockDetails> list = stockDetailsTextFileParserService.parseStockDetailsByStockFromTxtFile(stock, st);
        assertTrue("size of the content should be 20", list.size() == 20);
    }
}
