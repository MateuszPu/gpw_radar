package com.gpw.radar.service.stockFinanceEvents;

import com.gpw.radar.Application;
import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockFinanceEvent;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.service.parser.web.stockFinanceEvent.StockwatchParserService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class StockwatchParserServiceTest {

    @Inject
    private StockRepository stockRepository;

    @Inject
    private StockwatchParserService stockwatchParserService;

    private List<Document> documents = new ArrayList<>();

    @Before
    public void init() {
        initDB();
        initDocs();
    }

    private void initDB() {
        stockRepository.deleteAll();
        Stock stock = new Stock();
        stock.setTicker(StockTicker.pkn);
        stock.setStockShortName("pknorlen");
        stockRepository.save(stock);
    }

    private void initDocs() {
        String stockFinanceEventPath_1 = "/stocks_data/stock_finance_event/exampleOfStockFinanceEvent_1.html";
        String stockFinanceEventPath_2 = "/stocks_data/stock_finance_event/exampleOfStockFinanceEvent_2.html";
        InputStream inputStreamOfstockFinanceEventPath_1 = getClass().getResourceAsStream(stockFinanceEventPath_1);
        InputStream inputStreamOfstockFinanceEventPath_2 = getClass().getResourceAsStream(stockFinanceEventPath_2);
        try {
            Document doc_1 = Jsoup.parse(inputStreamOfstockFinanceEventPath_1, null, "uri cannot be null");
            Document doc_2 = Jsoup.parse(inputStreamOfstockFinanceEventPath_2, null, "uri cannot be null");
            documents.add(doc_1);
            documents.add(doc_2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void stockFiveMinutesDetailsSizeByTime() {
        List<StockFinanceEvent> listOfEvents = stockwatchParserService.getStockFinanceEvents(documents);
        assertThat(listOfEvents.size()).isEqualTo(2);
        assertThat(listOfEvents.stream().findFirst().get().getDate()).isEqualTo(LocalDate.of(2016, 01, 28));
    }
}
