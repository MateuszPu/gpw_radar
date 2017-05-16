package com.gpw.radar.rabbitmq.consumer.rss.news;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

public class MessageFactory {

    public static Message createRabbitMessage() {
        String jsonMessageFirst = "{\"newsDateTime\":\"2016-08-04T20:14:00\",\"message\":\"test message\",\"link\":\"http://www.twiter.com/\"}";
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.getHeaders().put("testHeader", RssType.EBI.name());
        Message msg = new Message(jsonMessageFirst.getBytes(), messageProperties);
        return msg;
    }
}

