package com.gpw.radar.config;

import com.gpw.radar.aop.exception.RabbitExceptionHandlerAspect;
import com.gpw.radar.aop.logging.LoggingAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableAspectJAutoProxy
public class LoggingAspectConfiguration {

    @Bean
    @Profile(Constants.SPRING_PROFILE_DEVELOPMENT)
    public LoggingAspect loggingAspect() {
        return new LoggingAspect();
    }

    @Bean
    public RabbitExceptionHandlerAspect rabbitExceptionHandlerAspect() {
        return new RabbitExceptionHandlerAspect();
    }
}
