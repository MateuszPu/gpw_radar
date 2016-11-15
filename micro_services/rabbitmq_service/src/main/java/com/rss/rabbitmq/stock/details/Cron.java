package com.rss.rabbitmq.stock.details;

import com.rss.rabbitmq.service.JsonConverter;
import com.stock.details.updater.model.StockDetails;
import com.stock.details.updater.parser.gpw.GpwSiteParser;
import com.stock.details.updater.parser.gpw.HtmlParser;
import com.stock.details.updater.parser.gpw.WebStockDetailsParser;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service("stockDetailsCron")
public class Cron {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final Sender sender;
    private final HtmlParser htmlParser;
    private final WebStockDetailsParser gpwParser;
    private final JsonConverter<StockDetails> jsonConverter;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Autowired
    public Cron(@Qualifier("stockDetailsSender") Sender sender, JsonConverter<StockDetails> jsonConverter) {
        this.sender = sender;
        this.jsonConverter = jsonConverter;
        this.htmlParser = new HtmlParser();
        this.gpwParser = new GpwSiteParser();
    }

    @Scheduled(cron = "0 30 17 ? * MON-FRI")
    public void fireCron() throws IOException {
        Elements tableRowsContentFromWeb = htmlParser.getTableRowsContentFromWeb();
        LocalDate currentDateOfStockDetails = htmlParser.getCurrentDateOfStockDetails();
        String stockDetailsDate = currentDateOfStockDetails.format(formatter);
        List<StockDetails> currentStockDetails = gpwParser.getCurrentStockDetails(tableRowsContentFromWeb, currentDateOfStockDetails);
        String json = jsonConverter.convertToJson(currentStockDetails);
        sender.send(json, stockDetailsDate);
    }
}
