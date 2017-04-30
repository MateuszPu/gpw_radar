package com.gpw.radar.rabbitmq.consumer.rss.news.mail;

import com.gpw.radar.aop.exception.RabbitExceptionHandler;
import com.gpw.radar.config.Constants;
import com.gpw.radar.elasticsearch.newsmessage.NewsMessage;
import com.gpw.radar.rabbitmq.consumer.rss.news.MessageTransformer;
import com.gpw.radar.service.mail.MailService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service("rssMailConsumer")
@Profile({Constants.SPRING_PROFILE_PRODUCTION, Constants.SPRING_PROFILE_DEVELOPMENT})
public class Consumer {

    private final MailService mailService;
    private final String newsTypeHeader;
    private final MessageTransformer messageTransformer;

    @Autowired
    public Consumer(MailService mailService, @Value("${rss_reader_news_type_header}") String newsTypeHeader,
                    MessageTransformer messageTransformer) {
        this.mailService = mailService;
        this.newsTypeHeader = newsTypeHeader;
        this.messageTransformer = messageTransformer;
    }

    @RabbitListener(queues = "${rss_reader_mail_queue}")
    @RabbitExceptionHandler
    public void consumeMessage(Message message) throws IOException {
        List<NewsMessage> newsMessages = messageTransformer.transformMessage(message, newsTypeHeader);
        newsMessages.stream().filter(e -> e.getStock() != null).forEach(mailService::informUserAboutStockNews);
    }

}
