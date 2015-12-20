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
public class StockwatchParserService implements StockFinanceEventParser{

    private final Logger logger = LoggerFactory.getLogger(StockwatchParserService.class);

    @Inject
    private DateAndTimeParserService dateAndTimeParserService;

    @Inject
    private StockRepository stockRepository;

    @Inject
    private SocketMessageService socketMessageService;

    private List<Stock> stockInApp;
    private double step;
    private int sizeOfEvents;

    public List<StockFinanceEvent> getStockFinanceEventFromWeb() {
        step = 0.0;
        sizeOfEvents = 0;
        List<StockFinanceEvent> stockFinanceEventParsed = new ArrayList<>();
        List<Elements> financeEventsElements = getAllFinanceEvents();
        stockInApp = stockRepository.findAll();

        for (int i = 0; i < financeEventsElements.size(); i++) {
            stockFinanceEventParsed.addAll(financeEventsElements.get(i)
                .stream()
                .map(e -> mapToStockFinanceEvent(e))
                .filter(e -> e.isPresent())
                .map(e -> e.get())
                .collect(Collectors.toList()));
        }

        return stockFinanceEventParsed;
    }

    private Optional<StockFinanceEvent> mapToStockFinanceEvent(Element e) {
        Double percentOfFill = (++step/sizeOfEvents) * 100.0;
        socketMessageService.sendStepOfFillDatabaseToClient(percentOfFill);

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

    private List<Elements> getAllFinanceEvents() {
        int i = 0;
        Document doc = null;
        List<Elements> trElements = new ArrayList<>();

        try {
            doc = getDocumentFromStockWatchWeb(i);
            while (!doc.select("table").isEmpty()) {
                Elements tr = doc.select("tbody").select("tr");
                trElements.add(tr);
                i++;
                sizeOfEvents += tr.size();
                doc = getDocumentFromStockWatchWeb(i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return trElements;
    }

    private Document getDocumentFromStockWatchWeb(int page) throws IOException {
        String url = "http://www.stockwatch.pl/async/CompaniesReportsView.aspx?p=all&t=all&pg=" + page;
        Document doc = Jsoup.connect(url).get();
        return doc;
    }
}
