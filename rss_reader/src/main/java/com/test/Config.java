package com.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public Sender sender() {
        System.out.println("-----------------");
        return new Sender();
    }
}
