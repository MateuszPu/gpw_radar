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

        StockDetails magnaStock = stockDetails.stream().filter(e -> e.getStock().getTicker().equals("06n")).findAny().get();
        assertThat(magnaStock.getStock().getStockShortName()).isEqualTo("06MAGNA");
        assertThat(magnaStock.getStock().getTicker()).isEqualTo("06n");
        assertThat(magnaStock.getDate()).isEqualTo(date);
        assertThat(magnaStock.getOpenPrice()).isEqualTo(new BigDecimal("1.95"));
        assertThat(magnaStock.getMaxPrice()).isEqualTo(new BigDecimal("1.99"));
        assertThat(magnaStock.getMinPrice()).isEqualTo(new BigDecimal("1.95"));
        assertThat(magnaStock.getClosePrice()).isEqualTo(new BigDecimal("1.99"));
        assertThat(magnaStock.getTransactionsNumber()).isEqualTo(5L);
        assertThat(magnaStock.getVolume()).isEqualTo(206L);

        StockDetails agrowillStock = stockDetails.stream().filter(e -> e.getStock().getTicker().equals("awg")).findAny().get();
        assertThat(agrowillStock.getStock().getStockShortName()).isEqualTo("AGROWILL");
        assertThat(agrowillStock.getStock().getTicker()).isEqualTo("awg");
        assertThat(agrowillStock.getDate()).isEqualTo(date);
        assertThat(agrowillStock.getOpenPrice()).isEqualTo(new BigDecimal("1.39"));
        assertThat(agrowillStock.getMaxPrice()).isEqualTo(new BigDecimal("1.39"));
        assertThat(agrowillStock.getMinPrice()).isEqualTo(new BigDecimal("1.39"));
        assertThat(agrowillStock.getClosePrice()).isEqualTo(new BigDecimal("1.39"));
        assertThat(agrowillStock.getTransactionsNumber()).isEqualTo(0L);
        assertThat(agrowillStock.getVolume()).isEqualTo(0L);

        StockDetails attStock = stockDetails.stream().filter(e -> e.getStock().getTicker().equals("att")).findAny().get();

        assertThat(attStock.getStock().getStockShortName()).isEqualTo("GRUPAAZOTY");
        assertThat(attStock.getStock().getTicker()).isEqualTo("att");
        assertThat(attStock.getDate()).isEqualTo(date);
        assertThat(attStock.getOpenPrice()).isEqualTo(new BigDecimal("4133.05"));
        assertThat(attStock.getMaxPrice()).isEqualTo(new BigDecimal("4133.05"));
        assertThat(attStock.getMinPrice()).isEqualTo(new BigDecimal("4013.00"));
        assertThat(attStock.getClosePrice()).isEqualTo(new BigDecimal("4096.55"));
        assertThat(attStock.getTransactionsNumber()).isEqualTo(863L);
        assertThat(attStock.getVolume()).isEqualTo(1721L);

        Optional<StockDetails> grajewoPdaStock = stockDetails.stream().filter(e -> e.getStock().getTicker().equals("grj")).findAny();
        assertThat(grajewoPdaStock.isPresent()).isEqualTo(false);
    }
}
