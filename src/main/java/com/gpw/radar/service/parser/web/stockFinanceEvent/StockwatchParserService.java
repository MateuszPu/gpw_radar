package com.gpw.radar.service.parser.web.stockFinanceEvent;

import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockFinanceEvent;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.service.parser.DateAndTimeParserService;
import com.gpw.radar.service.sockets.SocketMessageService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//data getting from stock watch website
@Component("stockwatchParserService")
public class StockwatchParserService implements StockFinanceEventParser {

    private final Logger logger = LoggerFactory.getLogger(StockwatchParserService.class);

    @Inject
    private DateAndTimeParserService dateAndTimeParserService;

    @Inject
    private StockRepository stockRepository;

    private List<Stock> stockInApp;

    public List<StockFinanceEvent> getStockFinanceEventFromWeb() {
        return getStockFinanceEvents(getDocumentsFromStockWatchWeb());
    }

    public List<StockFinanceEvent> getStockFinanceEvents(List<Document> documents) {
        List<StockFinanceEvent> stockFinanceEventParsed = new ArrayList<>();
        List<Elements> financeEventsElements = getAllFinanceEvents(documents);
        stockInApp = stockRepository.findAll();

        financeEventsElements.forEach(elements -> stockFinanceEventParsed.addAll(elements
            .stream()
            .map(e -> mapToStockFinanceEvent(e))
            .filter(element -> element.isPresent())
            .map(element -> element.get())
            .collect(Collectors.toList())));

        return stockFinanceEventParsed;
    }

    private Optional<StockFinanceEvent> mapToStockFinanceEvent(Element e) {
        StockFinanceEvent stockFinanceEvent = new StockFinanceEvent();
        String stockShortName = e.getElementsByClass("l").first().text().toLowerCase();

        Optional<Stock> stock = stockInApp.stream()
            .filter(st -> st.getStockShortName().toLowerCase().equals(stockShortName))
            .findAny();

        if (!stock.isPresent()) {
            return Optional.empty();
        }

        LocalDate date = dateAndTimeParserService.parseLocalDateFromString(e.getElementsByClass("c").first().text());
        String message = e.getElementsByClass("r").first().text();
        stockFinanceEvent.setDate(date);
        stockFinanceEvent.setMessage(message);
        stockFinanceEvent.setStock(stock.get());

        return Optional.of(stockFinanceEvent);
    }

    private List<Elements> getAllFinanceEvents(List<Document> documents) {
        List<Elements> trElements = new ArrayList<>();

        for(Document element: documents) {
            Elements tr = element.select("tbody").select("tr");
            trElements.add(tr);
        }
        return trElements;
    }

    private List<Document> getDocumentsFromStockWatchWeb() {
        List<Document> documents = new ArrayList<>();
        int i = 0;
        try {
            Document doc = getDocumentFromStockWatchWeb(i);
            while (!doc.select("table").isEmpty()) {
                documents.add(doc);
                doc = getDocumentFromStockWatchWeb(i++);
            }
        } catch (IOException e) {
            logger.error("Error occurs: " + e);
        }

        return documents;
    }

    private Document getDocumentFromStockWatchWeb(int page) throws IOException {
        String url = "http://www.stockwatch.pl/async/CompaniesReportsView.aspx?p=all&t=all&pg=" + page;
        Document doc = Jsoup.connect(url).get();
        return doc;
    }
}
