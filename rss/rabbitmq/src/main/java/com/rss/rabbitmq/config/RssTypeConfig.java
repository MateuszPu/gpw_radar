package com.rss.rabbitmq.config;

import com.rss.rabbitmq.types.RssType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RssTypeConfig {

    @Bean
    private Map<RssType, LocalDateTime> rssTypes() {
        Map<RssType, LocalDateTime> rssLinks = new HashMap<>();
        LocalDateTime date = LocalDateTime.now();
        rssLinks.put(RssType.CHALLENGE, date);
        rssLinks.put(RssType.EBI, date);
        rssLinks.put(RssType.ESPI, date);
        rssLinks.put(RssType.PAP, date);
        rssLinks.put(RssType.RECOMMENDATIONS, date);
        rssLinks.put(RssType.RESULTS, date);
        return rssLinks;
    }
}
