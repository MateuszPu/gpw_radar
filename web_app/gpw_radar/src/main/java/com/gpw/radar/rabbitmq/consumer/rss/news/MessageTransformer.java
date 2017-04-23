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
import java.util.stream.Collectors;

@Service
public class MessageTransformer {

    private final StockRepository stockRepository;
    private final JsonTransformer<NewsMessage> jsonTransformer = new JsonTransformer<NewsMessage>();

    @Autowired
    public MessageTransformer(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public List<ChatMessage> transformMessage(Message message) throws IOException {
        List<NewsMessage> newsMessages = jsonTransformer.deserializeFromJson(message, NewsMessage.class);
        List<ChatMessage> chatMessages = newsMessages.stream().map(this::createChatMessage).collect(Collectors.toList());
        chatMessages.forEach(e -> e.setMessage(transformToChatMessageContent(e.getLink(), e.getMessage())));
        chatMessages.forEach(e -> {
            User user = new User();
            user.setId("h6ehbr4khohjr116k23pon9vojv66c3eab45aui6pmau3acq1b");
            user.setLogin("system");
            e.setUser(user);
        });
        return chatMessages;
    }

    private ChatMessage createChatMessage(NewsMessage e) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setLink(e.getLink());
        chatMessage.setMessage(e.getMessage());
        return chatMessage;
    }

    public List<NewsMessage> transformMessage(Message message, String newsTypeHeader) throws IOException {
        RssType type = RssType.valueOf((String) message.getMessageProperties().getHeaders().get(newsTypeHeader));
        List<NewsMessage> newsMessages = jsonTransformer.deserializeFromJson(message, NewsMessage.class);
        newsMessages.forEach(e -> e.setType(type));
        newsMessages.forEach(e -> e.setStock(getStockFromTitle(e.getMessage()).orElse(null)));
        if (type.equals(RssType.PAP)) {
            newsMessages.forEach(e -> e.setNewsDateTime(e.getNewsDateTime().plusHours(1)));
        }
        return newsMessages;
    }

    private Optional<Stock> getStockFromTitle(String message) {
        Optional<Stock> result = Optional.empty();
        Pattern pattern = Pattern.compile("^([\\p{javaUpperCase}0-9-/.]+ )+");
        String trim = message.trim();
        Matcher matcher = pattern.matcher(trim);
        if (matcher.find()) {
            Optional<com.gpw.radar.domain.stock.Stock> stock = stockRepository.findByStockName(matcher.group(0).trim());
            if (stock.isPresent()) {
                com.gpw.radar.domain.stock.Stock dbStock = stock.get();
                Stock st = new Stock(dbStock.getTicker(), dbStock.getStockName(), dbStock.getStockShortName());
                result = Optional.of(st);
            }
        }
        return result;
    }

    public String transformToChatMessageContent(String link, String message) {
        StringBuilder str = new StringBuilder();
        str.append("<a href=\"");
        str.append(link);
        str.append("\" target=\"_blank\">");
        str.append(message);
        str.append("</a>");
        return str.toString();
    }
}
