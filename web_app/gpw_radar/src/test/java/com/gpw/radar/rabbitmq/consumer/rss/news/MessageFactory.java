package com.gpw.radar.rabbitmq.consumer.rss.news;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

public class MessageFactory {

    public static Message createRabbitMessage() {
        String jsonMessageFirst = "{\"newsDateTime\":\"2016-08-04T20:14:00\",\"message\":\"test message\",\"link\":\"http://www.twiter.com/\"}";
        String jsonMessageSecond = "{\"newsDateTime\":\"2016-08-01T20:00:00\",\"message\":\"RAWLPLUG SA test message two\",\"link\":\"http://www.google.pl/\"}";
        String message = "[" + jsonMessageFirst + ", " + jsonMessageSecond + "]";
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.getHeaders().put("testHeader", RssType.EBI.name());
        Message msg = new Message(message.getBytes(), messageProperties);
        return msg;
    }
}

