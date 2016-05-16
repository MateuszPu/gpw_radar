package com.gpw.radar.service.stockDetails.dailyUpdate;

import com.gpw.radar.Application;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.service.auto.update.stockDetails.GpwParser;
import com.gpw.radar.service.builders.StockBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class GPWParserTest {

    @Inject
    private GpwParser gpwParser;

    @Inject
    private StockRepository stockRepository;

    private InputStream inputStreamOfStockDetails;
    private BufferedReader bufferedReader;
    private Document doc;

    @Before
    public void init() throws IOException {
        prepareStockInDb();
        prepareHtmlFile();
    }

    private void prepareStockInDb() {
        List<Stock> stocks = new LinkedList<>();
        Stock abcStock = StockBuilder.sampleStock().ticker("abc").build();
        Stock froStock = StockBuilder.sampleStock().ticker("fro").build();
        stocks.add(froStock);
        stocks.add(abcStock);
        stockRepository.save(stocks);
    }

    private void prepareHtmlFile() {
        String exampleDataPath = "/stocks_data/daily/pl/wse_stocks/daily_gpw_site.html";
        inputStreamOfStockDetails = getClass().getResourceAsStream(exampleDataPath);
        bufferedReader = new BufferedReader(new InputStreamReader(inputStreamOfStockDetails));
        StringBuilder sb = new StringBuilder();
        bufferedReader.lines().forEach(line -> sb.append(line));
        doc = Jsoup.parse(sb.toString());
    }

    @After
    public void close() throws IOException {
        inputStreamOfStockDetails.close();
        bufferedReader.close();
    }

    @Test
    public void stockDetailsResult() throws IOException {
        List<StockDetails> stockDetails = gpwParser.getStockDetailsFromWeb(doc, LocalDate.of(2016, 3, 9));
        Stock stock = StockBuilder.sampleStock().build();
        stockDetails.stream().filter(details -> details.getStock() == null).forEach(det -> det.setStock(stock));

        StockDetails froStockDetails = stockDetails.stream().filter(details -> details.getStock().getTicker().equals("fro")).findAny().get();
        assertThat(froStockDetails.getVolume()).isEqualTo(1020);
        assertThat(froStockDetails.getClosePrice()).isEqualTo(new BigDecimal("10.79"));
        assertThat(froStockDetails.getMinPrice()).isEqualTo(new BigDecimal("10.79"));
        assertThat(froStockDetails.getMaxPrice()).isEqualTo(new BigDecimal("10.98"));
        assertThat(froStockDetails.getOpenPrice()).isEqualTo(new BigDecimal("10.98"));
        assertThat(froStockDetails.getDate()).isEqualTo(LocalDate.of(2016, 3, 9));

        StockDetails abcStockDetails = stockDetails.stream().filter(details -> details.getStock().getTicker().equals("abc")).findAny().get();
        assertThat(abcStockDetails.getVolume()).isEqualTo(14061);
        assertThat(abcStockDetails.getClosePrice()).isEqualTo(new BigDecimal("3.33"));
        assertThat(abcStockDetails.getMinPrice()).isEqualTo(new BigDecimal("3.29"));
        assertThat(abcStockDetails.getMaxPrice()).isEqualTo(new BigDecimal("3.35"));
        assertThat(abcStockDetails.getOpenPrice()).isEqualTo(new BigDecimal("3.33"));
        assertThat(abcStockDetails.getDate()).isEqualTo(LocalDate.of(2016, 3, 9));
    }

}
