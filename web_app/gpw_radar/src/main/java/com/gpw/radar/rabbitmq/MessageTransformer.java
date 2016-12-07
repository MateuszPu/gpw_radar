package com.gpw.radar.rabbitmq;

import com.gpw.radar.domain.rss.NewsMessage;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.rabbitmq.consumer.rss.news.GpwNewsModel;
import com.gpw.radar.rabbitmq.consumer.rss.news.RssType;
import com.gpw.radar.repository.stock.StockRepository;
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
    private final Mapper<GpwNewsModel, NewsMessage> mapper;

    @Autowired
    public MessageTransformer(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
        mapper = new Mapper();
    }

    public List<NewsMessage> getNewsMessages(Message message, String newsTypeHeader) throws IOException {
        RssType type = RssType.valueOf((String) message.getMessageProperties().getHeaders().get(newsTypeHeader));
        List<NewsMessage> newsMessages = mapper.transformFromJsonToDomainObject(message, GpwNewsModel.class, NewsMessage.class);
        newsMessages.stream().forEach(e -> e.setType(type));
        newsMessages.stream().forEach(e -> e.setStock(getStockFromTitle(e.getMessage()).orElse(null)));
        if(type.equals(RssType.PAP)) {
            newsMessages.forEach(e -> e.setNewsDateTime(e.getNewsDateTime().plusHours(1)));
        }
        return newsMessages;
    }

    private Optional<Stock> getStockFromTitle(String message) {
        Pattern pattern = Pattern.compile("^([\\p{javaUpperCase}0-9-/.]+ )+");
        String trim = message.trim();
        Matcher matcher = pattern.matcher(trim);
        if (matcher.find()) {
            Optional<Stock> stock = stockRepository.findByStockName(matcher.group(0).trim());
            if (stock.isPresent()) {
                return Optional.of(stock.get());
            }
        }
        return Optional.empty();
    }

    public String transformMessage(String link, String message) {
        StringBuilder str = new StringBuilder();
        str.append("<a href=\"");
        str.append(link);
        str.append("\" target=\"_blank\">");
        str.append(message);
        str.append("</a>");
        return str.toString();
    }
}
