package com.gpw.radar.service.auto.update.stockDetails;

import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockDetails;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.service.parser.web.UrlStreamsGetterService;
import com.gpw.radar.service.parser.web.stock.StockBatchWebParser;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//getting data from http://www.gpw.pl/akcje_i_pda_notowania_ciagle_pelna_wersja#all
@Component("gpwParser")
public class GpwParser implements StockDetailsParser {

    private final Logger logger = LoggerFactory.getLogger(GpwParser.class);

    @Inject
    private StockRepository stockRepository;

    @Inject
    private UrlStreamsGetterService urlStreamsGetterService;

    private static final int indexOfTicker = 3;
    private static final int indexOfOpenPrice = 8;
    private static final int indexOfMinPrice = 9;
    private static final int indexOfMaxPrice = 10;
    private static final int indexOfClosePrice = 11;
    private final int indexOfTransactionsNumber = 20;
    private static final int indexOfVolume = 21;
    private static final int indexOfLastClosePrice = 6;
    private static final DateTimeFormatter dtfType = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Override
    public List<StockDetails> getCurrentStockDetails() {
        LocalDate date = getCurrentDateOfStockDetails();
        return getStockDetailsFromWeb(new Document("as"), date);
    }

    public List<StockDetails> getStockDetailsFromWeb(Document doc, LocalDate date) {
        Elements tableRows = doc.select("tr");
        List<StockDetails> stockDetails = new ArrayList<StockDetails>();
        Set<String> tickers = stockRepository.findAllTickers();
        String ticker;

        // start from index 2 to skip table title
        for (int index = 2; index < tableRows.size() - 1; index++) {

            // skip the table title showing every 20 stock details
            if (index % 22 == 0) {
                index++;
                continue;
            }
            Elements select = tableRows.get(index).select("td");
            ticker = select.get(indexOfTicker).text().toLowerCase();
            if (!tickers.contains(ticker)) {
                continue;
            }

            StockDetails std = new StockDetails();
            Stock stock = stockRepository.findByTicker(ticker);
            std.setStock(stock);

            std.setDate(date);
            try {
                std.setOpenPrice(new BigDecimal(getElement(select, indexOfOpenPrice)));
                std.setMaxPrice(new BigDecimal(getElement(select, indexOfMaxPrice)));
                std.setMinPrice(new BigDecimal(getElement(select, indexOfMinPrice)));
                std.setClosePrice(new BigDecimal(getElement(select, indexOfClosePrice)));
                std.setTransactionsNumber(Long.valueOf(getElement(select, indexOfTransactionsNumber)));
                std.setVolume(Long.valueOf(getElement(select, indexOfVolume)));
            } catch (NumberFormatException ex) {
                // if string is not a valid presentation of number that means
                // the stockdetails was not quoted and we have to set last price to all details
                std.setOpenPrice(new BigDecimal(getElement(select, indexOfLastClosePrice)));
                std.setMaxPrice(new BigDecimal(getElement(select, indexOfLastClosePrice)));
                std.setMinPrice(new BigDecimal(getElement(select, indexOfLastClosePrice)));
                std.setClosePrice(new BigDecimal(getElement(select, indexOfLastClosePrice)));
                std.setTransactionsNumber(0L);
                std.setVolume(0l);
            }
            stockDetails.add(std);
        }
        return stockDetails;
    }

    private String getElement(Elements select, int indexOfElement) {
        return select.get(indexOfElement).text().replace(",", ".").replace("\u00a0", "");
    }

    private LocalDate getCurrentDateOfStockDetails() {
        Document doc = urlStreamsGetterService.getDocFromUrl("http://www.gpw.pl/akcje_i_pda_notowania_ciagle_pelna_wersja#all");
        Elements el = doc.select("div[class=\"colFL\"]");
        LocalDate date = LocalDate.parse(el.first().text(), dtfType);
        return date;
    }

    @Override
    public void setQuotesDate(LocalDate quotesDate) {
        throw new UnsupportedOperationException("method should be not override");
    }
}
