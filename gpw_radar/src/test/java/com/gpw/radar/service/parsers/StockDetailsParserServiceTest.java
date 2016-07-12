package com.gpw.radar.service.parsers;

import com.gpw.radar.Application;
import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.service.parser.web.stockDetails.StockDetailsParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class StockDetailsParserServiceTest {

    @Autowired
    StockDetailsParser stockDetailsParser;

    @Test
    public void test() {
        LocalDate testDate = LocalDate.of(2016, 7, 8);
        List<StockDetails> details = stockDetailsParser.parseStockDetails(testDate);
        StockDetails stockDetails = details.stream().filter(dt -> dt.getStock().getStockShortName().equals("KGHM")).findAny().get();

        assertThat(stockDetails.getDate()).isEqualTo(testDate);
        assertThat(stockDetails.getVolume()).isEqualTo(1016818);
        assertThat(stockDetails.getOpenPrice()).isEqualTo(new BigDecimal("68"));
        assertThat(stockDetails.getMaxPrice()).isEqualTo(new BigDecimal("68,6"));
        assertThat(stockDetails.getMinPrice()).isEqualTo(new BigDecimal("67,68"));
        assertThat(stockDetails.getClosePrice()).isEqualTo(new BigDecimal("68,01"));
        assertThat(stockDetails.getTransactionsNumber()).isEqualTo(5841);
    }
}
