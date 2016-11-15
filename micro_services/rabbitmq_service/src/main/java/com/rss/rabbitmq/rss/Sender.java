package com.rss.rabbitmq.rss;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service("rssService")
public class Sender {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Value("${rss_reader_fanout_exchange}")
    private String name;

    @Value("${rss_reader_news_type_header}")
    private String newsType;

    private final RabbitTemplate template;

    @Autowired
    public Sender(RabbitTemplate template) {
        this.template = template;
    }

    public void send(String newses, String rssChannelName) {
        try {
            Message message = MessageBuilder.withBody(newses.getBytes("UTF-8"))
                    .setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
                    .setHeader(newsType, rssChannelName)
                    .build();
            this.template.convertAndSend(name, "", message);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Exception in "
                    + this.getClass().getName()
                    + " with clause : "
                    + e.getCause());
        }
    }
}