package com.stock.details.updater.parser;

import com.stock.details.updater.model.StockDetails;
import com.stock.details.updater.parser.gpw.GpwParser;
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

import static org.assertj.core.api.StrictAssertions.assertThat;


public class GpwParserTest {

    private Elements rowElements;

    @Before
    public void init() throws IOException {
        ClassLoader classLoader = GpwParserTest.class.getClassLoader();
        File file = new File(classLoader.getResource("stock_details.html").getFile());
        Document doc = Jsoup.parse(file, "UTF-8");
        rowElements = doc.select("tr");
    }

    @Test
    public void getStockDetailsFromElements() throws IOException {
        GpwParser parser = new GpwParser();
        LocalDate date = LocalDate.of(2016, 1, 1);
        List<StockDetails> stockDetails = parser.getCurrentStockDetails(rowElements, date);

        StockDetails firstStockDetails = stockDetails.get(0);
        assertThat(firstStockDetails.getStockName()).isEqualTo("06MAGNA");
        assertThat(firstStockDetails.getStockTicker()).isEqualTo("06N");
        assertThat(firstStockDetails.getDate()).isEqualTo(date);
        assertThat(firstStockDetails.getOpenPrice()).isEqualTo(new BigDecimal("1.95"));
        assertThat(firstStockDetails.getMaxPrice()).isEqualTo(new BigDecimal("1.99"));
        assertThat(firstStockDetails.getMinPrice()).isEqualTo(new BigDecimal("1.95"));
        assertThat(firstStockDetails.getClosePrice()).isEqualTo(new BigDecimal("1.99"));
        assertThat(firstStockDetails.getTransactionsNumber()).isEqualTo(5L);
        assertThat(firstStockDetails.getVolume()).isEqualTo(206L);

        StockDetails agrowillStock = stockDetails.stream().filter(e -> e.getStockName().equals("AGROWILL")).findAny().get();
        assertThat(agrowillStock.getStockName()).isEqualTo("AGROWILL");
        assertThat(agrowillStock.getStockTicker()).isEqualTo("AWG");
        assertThat(agrowillStock.getDate()).isEqualTo(date);
        assertThat(agrowillStock.getOpenPrice()).isEqualTo(new BigDecimal("1.39"));
        assertThat(agrowillStock.getMaxPrice()).isEqualTo(new BigDecimal("1.39"));
        assertThat(agrowillStock.getMinPrice()).isEqualTo(new BigDecimal("1.39"));
        assertThat(agrowillStock.getClosePrice()).isEqualTo(new BigDecimal("1.39"));
        assertThat(agrowillStock.getTransactionsNumber()).isEqualTo(0L);
        assertThat(agrowillStock.getVolume()).isEqualTo(0L);

        StockDetails attStock = stockDetails.stream().filter(e -> e.getStockTicker().equals("ATT")).findAny().get();

        assertThat(attStock.getStockName()).isEqualTo("GRUPAAZOTY");
        assertThat(attStock.getStockTicker()).isEqualTo("ATT");
        assertThat(attStock.getDate()).isEqualTo(date);
        assertThat(attStock.getOpenPrice()).isEqualTo(new BigDecimal("4133.05"));
        assertThat(attStock.getMaxPrice()).isEqualTo(new BigDecimal("4133.05"));
        assertThat(attStock.getMinPrice()).isEqualTo(new BigDecimal("4013.00"));
        assertThat(attStock.getClosePrice()).isEqualTo(new BigDecimal("4096.55"));
        assertThat(attStock.getTransactionsNumber()).isEqualTo(863L);
        assertThat(attStock.getVolume()).isEqualTo(1721L);
    }
}
