package com.gpw.radar.rabbitmq.consumer.rss.news.database;

import com.gpw.radar.config.Constants;
import com.gpw.radar.dao.newsmessage.NewsMessageDAO;
import com.gpw.radar.elasticsearch.newsmessage.NewsMessage;
import com.gpw.radar.rabbitmq.consumer.rss.news.MessageTransformer;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service("rssDatabaseConsumer")
@Profile({Constants.SPRING_PROFILE_PRODUCTION, Constants.SPRING_PROFILE_DEVELOPMENT})
public class Consumer {

    private final String newsTypeHeader;
    private final NewsMessageDAO newsMessageRepository;
    private final MessageTransformer messageTransformer;

    @Autowired
    public Consumer(@Qualifier("newsMessageEsDAO") NewsMessageDAO newsMessageRepository,
                    @Value("${rss_reader_news_type_header}") String newsTypeHeader,
                    MessageTransformer messageTransformer) {
        this.newsMessageRepository = newsMessageRepository;
        this.messageTransformer = messageTransformer;
        this.newsTypeHeader = newsTypeHeader;
    }

    @RabbitListener(queues = "${rss_reader_database_queue}")
    public void consumeMessage(Message message) throws InterruptedException, IOException {
        List<NewsMessage> newsMessages = messageTransformer.transformMessage(message, newsTypeHeader);
        newsMessageRepository.save(newsMessages);
    }
}
