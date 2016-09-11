package com.stock.details.updater.parser.gpw;

import com.stock.details.updater.StockDetailsParser;
import com.stock.details.updater.model.StockDetails;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

//getting data from http://www.gpw.pl/akcje_i_pda_notowania_ciagle_pelna_wersja#all
public class GpwParser implements StockDetailsParser {

    private static final int STOCK_NAME_INDEX = 3;
    private static final int STOCK_TICKER_INDEX = 3;
    private static final int OPEN_PRICE_INDEX = 8;
    private static final int MIN_PRICE_INDEX = 9;
    private static final int MAX_PRICE_INDEX = 10;
    private static final int CLOSE_PRICE_INDEX = 11;
    private final int TRANSACTIONS_COUNT_INDEX = 20;
    private static final int VOULME_INDEX = 21;
    private static final int LAST_CLOSE_PRICE_INDEX = 6;
    private static final DateTimeFormatter dtfType = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Override
    public List<StockDetails> getCurrentStockDetails() throws IOException {
        LocalDate date = getCurrentDateOfStockDetails();
        return null;
    }

    public LocalDate getCurrentDateOfStockDetails() throws IOException {
        Document doc = Jsoup.connect("http://www.gpw.pl/akcje_i_pda_notowania_ciagle_pelna_wersja#all").get();
        Elements el = doc.select("div[class=\"colFL\"]");
        LocalDate date = LocalDate.parse(el.first().text(), dtfType);
        return date;
    }

}
