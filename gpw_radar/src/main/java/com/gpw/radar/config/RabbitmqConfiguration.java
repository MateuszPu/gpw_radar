package com.gpw.radar.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:rabbitmq_config.properties")
public class RabbitmqConfiguration {

}

