package com.gpw.radar.rabbitmq.consumer.rss.news.database;

import com.gpw.radar.config.Constants;
import com.gpw.radar.domain.rss.NewsMessage;
import com.gpw.radar.rabbitmq.MessageTransformer;
import com.gpw.radar.repository.rss.NewsMessageRepository;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service("rssDatabaseConsumer")
@Profile("!" + Constants.SPRING_PROFILE_FAST)
public class Consumer {

    private final String newsTypeHeader;
    private final NewsMessageRepository newsMessageRepository;
    private final MessageTransformer messageTransformer;

    @Autowired
    public Consumer(NewsMessageRepository newsMessageRepository,
                    @Value("${rss_reader_news_type_header}") String newsTypeHeader,
                    MessageTransformer messageTransformer) {
        this.newsMessageRepository = newsMessageRepository;
        this.messageTransformer = messageTransformer;
        this.newsTypeHeader = newsTypeHeader;
    }

    @RabbitListener(queues = "${rss_reader_database_queue}")
    public void reciveMessage(Message message) throws InterruptedException, IOException {
        List<NewsMessage> newsMessages = messageTransformer.getNewsMessages(message, newsTypeHeader);
        newsMessageRepository.save(new ArrayList<>(newsMessages));
    }
}
