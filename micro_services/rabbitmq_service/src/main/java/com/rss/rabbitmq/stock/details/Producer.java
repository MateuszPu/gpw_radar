package com.rss.rabbitmq.stock.details;

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

@Service("stockDetailsSender")
public class Producer {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Value("${stock_details_direct_exchange}")
    private String name;

    @Value("${stock_details_routing_key}")
    private String routingKey;

    @Value("${stock_details_date_header}")
    private String dateHeader;

    private final RabbitTemplate template;

    @Autowired
    public Producer(RabbitTemplate template) {
        this.template = template;
    }

    public void publish(String stockDetailsJson, String date) {
        try {
            Message message = MessageBuilder.withBody(stockDetailsJson.getBytes("UTF-8"))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setHeader(dateHeader, date)
                    .build();
            this.template.convertAndSend(name, routingKey, message);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Exception in {} with clause : {}", this.getClass().getName(), e.getCause());
        }
    }
}
