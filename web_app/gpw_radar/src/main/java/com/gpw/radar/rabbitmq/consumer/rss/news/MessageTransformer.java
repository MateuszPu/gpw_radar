package com.gpw.radar.rabbitmq.consumer.rss.news;

import com.gpw.radar.domain.User;
import com.gpw.radar.domain.chat.ChatMessage;
import com.gpw.radar.elasticsearch.newsmessage.NewsMessage;
import com.gpw.radar.elasticsearch.stockdetails.Stock;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.service.mapper.JsonTransformer;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MessageTransformer {

    private final StockRepository stockRepository;
    private final JsonTransformer<NewsMessage> jsonTransformer = new JsonTransformer<>();

    @Autowired
    public MessageTransformer(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public ChatMessage transformMessage(Message message) throws IOException {
        NewsMessage newsMessage = jsonTransformer.deserializeObjectFromJson(message, NewsMessage.class);
        ChatMessage chatMessage = new ChatMessage(newsMessage);
        chatMessage.setUser(User.createSystemUser());
        return chatMessage;
    }

    public NewsMessage transformMessage(Message message, String newsTypeHeader) throws IOException {
        RssType type = RssType.valueOf((String) message.getMessageProperties().getHeaders().get(newsTypeHeader));
        NewsMessage newsMessage = jsonTransformer.deserializeObjectFromJson(message, NewsMessage.class);
        newsMessage.setType(type);
        newsMessage.setStock(getStockFromMessage(newsMessage.getMessage()));
        //don't know why PAP messages have wrong time
        if (type.equals(RssType.PAP)) {
            newsMessage.setNewsDateTime(newsMessage.getNewsDateTime().plusHours(1));
        }
        return newsMessage;
    }

    private Stock getStockFromMessage(String message) {
        Stock result = null;
        Matcher matcher = getMatcher(message);
        if (matcher.find()) {
            Optional<com.gpw.radar.domain.stock.Stock> stock = stockRepository.findByStockName(matcher.group(0).trim());
            if (stock.isPresent()) {
                com.gpw.radar.domain.stock.Stock dbStock = stock.get();
                result = new Stock(dbStock.getTicker(), dbStock.getStockName(), dbStock.getStockShortName());
            }
        }
        return result;
    }

    private Matcher getMatcher(String message) {
        Pattern pattern = Pattern.compile("^([\\p{javaUpperCase}0-9-/.]+ )+");
        String trim = message.trim();
        return pattern.matcher(trim);
    }
}
