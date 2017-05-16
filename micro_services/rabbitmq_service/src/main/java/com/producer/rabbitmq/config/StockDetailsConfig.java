package com.producer.rabbitmq.config;

import com.stock.details.updater.parser.gpw.GpwSiteParser;
import com.stock.details.updater.parser.gpw.HtmlParser;
import com.stock.details.updater.parser.gpw.WebStockDetailsParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StockDetailsConfig {

    @Bean
    public HtmlParser htmlParser() {
        return new HtmlParser();
    }

    @Bean
    public WebStockDetailsParser gpwParser() {
        return new GpwSiteParser();
    }
}
