package com.gpw.radar.config.rabbitmq;

import com.gpw.radar.config.Constants;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:${spring.profiles.active}_rabbitmq_config.properties")
@Profile({Constants.SPRING_PROFILE_PRODUCTION, Constants.SPRING_PROFILE_DEVELOPMENT})
public class RabbitmqConfiguration {
}

