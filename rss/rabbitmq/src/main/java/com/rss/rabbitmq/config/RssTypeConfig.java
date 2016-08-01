package com.rss.rabbitmq.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class RssTypeConfig {

    @Bean(name = "rssTypesWithLink")
    public Map<RssType, LocalDateTime> rssTypesWithLink() {
        Map<RssType, LocalDateTime> rssLinks = new HashMap<>();
        List<RssType> rssTypes = Arrays.asList(RssType.values());
        rssTypes.forEach(e -> rssLinks.put(e, LocalDateTime.now().minusHours(3)));
        return rssLinks;
    }
}
