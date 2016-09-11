package com.stock.details.updater.parser.gpw;

import org.jsoup.select.Elements;
import org.junit.Test;
import static org.assertj.core.api.StrictAssertions.assertThat;

public class HtmlParserTest {

    @Test
    public void getTableRowsContentFromWebTest() {
        HtmlParser parser = new HtmlParser();
        Elements tableRowsContentFromWeb = parser.getTableRowsContentFromWeb();
        assertThat(tableRowsContentFromWeb.size()).isNotZero();
    }
}
