package com.rss.rabbitmq.sender;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class Sender {

    @Value("${rss_reader_fanout_exchange}")
    private String name;

    @Autowired
    private RabbitTemplate template;

    public void send() {
        String message = "Hello World!";
        this.template.convertAndSend(name, "", message);
        System.out.println(" [x] Sent '" + message + "'");
    }
}