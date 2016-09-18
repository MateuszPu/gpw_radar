package com.rss.rabbitmq.stock.details;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service("stockDetailsCron")
public class Cron {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final Sender sender;

    @Autowired
    public Cron(@Qualifier("stockDetailsSender") Sender sender) {
        this.sender = sender;
    }

    @Scheduled(cron = "0 30 17 ? * MON-FRI")
    public void fireCron() {

    }
}
