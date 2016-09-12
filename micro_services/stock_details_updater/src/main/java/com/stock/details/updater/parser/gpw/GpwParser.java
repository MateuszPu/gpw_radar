package com.stock.details.updater.parser.gpw;

import com.stock.details.updater.StockDetailsParser;
import com.stock.details.updater.model.StockDetails;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//getting data from http://www.gpw.pl/akcje_i_pda_notowania_ciagle_pelna_wersja#all
public class GpwParser implements StockDetailsParser {

    private static final int STOCK_NAME_INDEX = 2;
    private static final int STOCK_TICKER_INDEX = 3;
    private static final int OPEN_PRICE_INDEX = 8;
    private static final int MIN_PRICE_INDEX = 9;
    private static final int MAX_PRICE_INDEX = 10;
    private static final int CLOSE_PRICE_INDEX = 11;
    private static final int TRANSACTIONS_COUNT_INDEX = 20;
    private static final int VOULME_INDEX = 21;
    private static final int LAST_CLOSE_PRICE_INDEX = 6;
    private static final DateTimeFormatter dtfType = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final Pattern NUMBER_REGEX = Pattern.compile("\\d{1,5}\\.\\d{2}");


    @Override
    public List<StockDetails> getCurrentStockDetails(Elements tableRows, LocalDate date) throws IOException {
        List<StockDetails> stockDetails = new ArrayList<StockDetails>();

        for (int index = 2; index < tableRows.size(); index++) {

            // skip the table title showing every 20 stock details
            if (index % 22 == 0) {
                index++;
                continue;
            }
            Elements select = tableRows.get(index).select("td");

            StockDetails std = new StockDetails(getElement(select, LAST_CLOSE_PRICE_INDEX));
            std.setStockName(getElement(select, STOCK_NAME_INDEX));
            std.setStockTicker(getElement(select, STOCK_TICKER_INDEX));
            std.setDate(date);

            String openPrice = getElement(select, OPEN_PRICE_INDEX);
            Matcher matcher = NUMBER_REGEX.matcher(openPrice);

            if (matcher.matches()) {
                std.setOpenPrice(new BigDecimal(getElement(select, OPEN_PRICE_INDEX)));
                std.setMaxPrice(new BigDecimal(getElement(select, MAX_PRICE_INDEX)));
                std.setMinPrice(new BigDecimal(getElement(select, MIN_PRICE_INDEX)));
                std.setClosePrice(new BigDecimal(getElement(select, CLOSE_PRICE_INDEX)));
                std.setVolume(Long.valueOf(getElement(select, VOULME_INDEX)));
                std.setTransactionsNumber(Long.valueOf(getElement(select, TRANSACTIONS_COUNT_INDEX)));
            }
            stockDetails.add(std);
        }
        return stockDetails;
    }

    public LocalDate getCurrentDateOfStockDetails() throws IOException {
        Document doc = Jsoup.connect("http://www.gpw.pl/akcje_i_pda_notowania_ciagle_pelna_wersja#all").get();
        Elements el = doc.select("div[class=\"colFL\"]");
        LocalDate date = LocalDate.parse(el.first().text(), dtfType);
        return date;
    }

    private String getElement(Elements select, int indexOfElement) {
        return select.get(indexOfElement).text().replace(",", ".").replace("\u00a0", "").trim();
    }
}
