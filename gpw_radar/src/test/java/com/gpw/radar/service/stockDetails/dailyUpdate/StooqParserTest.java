package com.gpw.radar.service.stockDetails.dailyUpdate;

import com.gpw.radar.Application;
import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.service.auto.update.stockDetails.StooqParser;
import com.gpw.radar.service.builders.StockBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.StrictAssertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class StooqParserTest {

    @Inject
    private StooqParser stooqParser;

    private Stock stock;
    private InputStream inputStreamOfStockDetails;

    @Before
    public void init() {
        stock = StockBuilder.sampleStock().ticker(StockTicker.kgh).stockName("KGH").stockShortName("kgh").build();
        String exampleDataPath = "/stocks_data/daily/pl/wse_stocks/daily_stooq_site_kgh.csv";
        inputStreamOfStockDetails = getClass().getResourceAsStream(exampleDataPath);
    }

    @After
    public void close() throws IOException {
        inputStreamOfStockDetails.close();
    }

    @Test
    public void stockDetailsResult() throws IOException {
        stooqParser.setQuotesDate(LocalDate.of(2016, 3, 9));
        StockDetails stockDetails = stooqParser.parseFromWeb(inputStreamOfStockDetails, stock);

        assertThat(stockDetails.getVolume()).isEqualTo(1352910);
        assertThat(stockDetails.getClosePrice()).isEqualTo(new BigDecimal("73.75"));
        assertThat(stockDetails.getMinPrice()).isEqualTo(new BigDecimal("71.83"));
        assertThat(stockDetails.getMaxPrice()).isEqualTo(new BigDecimal("74.39"));
        assertThat(stockDetails.getOpenPrice()).isEqualTo(new BigDecimal("73"));
        assertThat(stockDetails.getDate()).isEqualTo(LocalDate.of(2016, 3, 9));
    }
}
