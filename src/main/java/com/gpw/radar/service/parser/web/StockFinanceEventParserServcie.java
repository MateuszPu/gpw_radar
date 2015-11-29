package com.gpw.radar.service.parser.web;

import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockFinanceEvent;
import com.gpw.radar.repository.stock.StockFinanceEventRepository;
import com.gpw.radar.service.parser.DateAndTimeParserService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.IOException;
import java.time.LocalDate;

@Service
public class StockFinanceEventParserServcie {

    private final Logger logger = LoggerFactory.getLogger(StockFinanceEventParserServcie.class);

    @Inject
    private StockFinanceEventRepository stockFinanceEventRepository;

    @Inject
    private DateAndTimeParserService dateAndTimeParserService;

    @Transactional
    public void getStockFinanceEvent(Stock stock) {
        Document doc = null;
        try {
            doc = getDocumentFromStockWatchWeb(stock.getStockShortName());
        } catch (IOException e) {
            logger.error("Error occurs: " + e.getMessage());
        }
        Elements elements = doc.select("div[class=CASld roundAll]");
        // if elements.size == 3 means that stock has any finance event
        // otherwise there is no finance event for stock
        if (elements.size() == 3) {
            Elements table = doc.select("table[class=cctabdt]");
            int indexOfCorrectTable = table.size() - 2;
            Element correctTable = table.get(indexOfCorrectTable);
            Elements tr = correctTable.select("tr");
            for (int i = 0; i < tr.size(); i++) {
                stockFinanceEventRepository.save(prepareStockFinanceEvent(stock, tr, i));
            }
        }
    }

    private StockFinanceEvent prepareStockFinanceEvent(Stock stock, Elements tr, int i) {
        final int positionOfDate = 0;
        final int positionOfMessage = 1;
        Elements select = tr.get(i).select("td");
        LocalDate date = dateAndTimeParserService.parseLocalDateFromString(select.get(positionOfDate).text());
        String message = select.get(positionOfMessage).text();

        StockFinanceEvent stockFinanceEvent = new StockFinanceEvent();
        stockFinanceEvent.setStock(stock);
        stockFinanceEvent.setDate(date);
        stockFinanceEvent.setMessage(message);
        return stockFinanceEvent;
    }

    private Document getDocumentFromStockWatchWeb(String stockShortName) throws IOException {
        String url = "http://www.stockwatch.pl/gpw/" + stockShortName + ",wykres-swiece,wskazniki.aspx";
        Document doc = Jsoup.connect(url).get();
        return doc;
    }
}
