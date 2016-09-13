package com.stock.details.updater.parser;

import com.stock.details.updater.parser.gpw.HtmlParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class HtmlParserTest {

    @Test
    public void getTableRowsContentFromWebTest() {
        HtmlParser parser = new HtmlParser();
        Elements tableRowsContentFromWeb = parser.getTableRowsContentFromWeb();
        assertThat(tableRowsContentFromWeb.size()).isNotZero();
    }

    @Test
    public void getDateFromGpwWebTest() throws IOException {
        HtmlParser parser = new HtmlParser();
        LocalDate currentDateOfStockDetails = parser.getCurrentDateOfStockDetails(getDoc());
        assertThat(currentDateOfStockDetails).isEqualTo(LocalDate.of(2016, 9, 13));
    }

    private Document getDoc() throws IOException {
        ClassLoader classLoader = GpwParserTest.class.getClassLoader();
        File file = new File(classLoader.getResource("gpw_main_site.html").getFile());
        Document doc = Jsoup.parse(file, "UTF-8");
        return doc;
    }
}
