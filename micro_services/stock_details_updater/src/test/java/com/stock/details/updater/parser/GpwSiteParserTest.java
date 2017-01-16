package com.stock.details.updater.parser;

import com.stock.details.updater.model.StockDetails;
import com.stock.details.updater.parser.gpw.GpwSiteParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.StrictAssertions.assertThat;


public class GpwSiteParserTest {

    private Elements rowElements;

    @Before
    public void init() throws IOException {
        ClassLoader classLoader = GpwSiteParserTest.class.getClassLoader();
        File file = new File(classLoader.getResource("stock_details.html").getFile());
        Document doc = Jsoup.parse(file, "UTF-8");
        rowElements = doc.select("tr");
    }

    @Test
    public void shouldParseStockDetailsFromWebSite() throws IOException {
        GpwSiteParser parser = new GpwSiteParser();
        LocalDate date = LocalDate.of(2016, 1, 1);
        List<StockDetails> stockDetails = parser.getCurrentStockDetails(rowElements, date);

        StockDetails agrowillStock = stockDetails.stream().filter(e -> e.getStock().getTicker().equals("awg")).findAny().get();

        AssertUtils.assertStockDetails(agrowillStock)
                .hasCorrectOpenPrice(new BigDecimal("1.39"))
                .hasCorrectClosePrice(new BigDecimal("1.39"))
                .hasCorrectMaxPrice(new BigDecimal("1.39"))
                .hasCorrectMinPrice(new BigDecimal("1.39"))
                .hasCorrectDate(date)
                .hasCorrectStockShortName("AGROWILL")
                .hasCorrectStockTicker("awg")
                .hasCorrectTransactionNumber(0L)
                .hasCorrectVolume(0L);

        StockDetails attStock = stockDetails.stream().filter(e -> e.getStock().getTicker().equals("att")).findAny().get();

        AssertUtils.assertStockDetails(attStock)
                    .hasCorrectOpenPrice(new BigDecimal("4133.05"))
                    .hasCorrectClosePrice(new BigDecimal("4096.55"))
                    .hasCorrectMaxPrice(new BigDecimal("4133.05"))
                    .hasCorrectMinPrice(new BigDecimal("4013.00"))
                    .hasCorrectDate(date)
                    .hasCorrectStockShortName("GRUPAAZOTY")
                    .hasCorrectStockTicker("att")
                    .hasCorrectTransactionNumber(863L)
                    .hasCorrectVolume(1721L);
    }

    @Test
    public void shouldNotParseStockDetailsWhichHasPda() throws IOException {
        GpwSiteParser parser = new GpwSiteParser();
        LocalDate date = LocalDate.of(2016, 1, 1);
        List<StockDetails> stockDetails = parser.getCurrentStockDetails(rowElements, date);

        Optional<StockDetails> attStock = stockDetails.stream().filter(e -> e.getStock().getTicker().equalsIgnoreCase("grja")).findAny();

        assertThat(attStock.isPresent()).isFalse();
    }

}
