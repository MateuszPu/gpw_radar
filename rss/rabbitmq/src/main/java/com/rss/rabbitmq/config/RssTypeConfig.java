package com.rss.rabbitmq.config;

import com.rss.parser.Parser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class RssTypeConfig {

    @Bean(name = "rssTypeTimeMap")
    public Map<RssType, LocalDateTime> rssTypeTimeMap() {
        Map<RssType, LocalDateTime> rssLinks = new HashMap<>();
        List<RssType> rssTypes = Arrays.asList(RssType.values());
        rssTypes.forEach(e -> rssLinks.put(e, LocalDateTime.now().minusDays(1)));
        return rssLinks;
    }

    @Bean(name = "rssTypeParserMap")
    public Map<RssType, Parser> rssTypeParserMap() {
        Map<RssType, Parser> rssTypeParserMap = rssTypeTimeMap().entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> new Parser(e.getKey().getUrl())));
        return rssTypeParserMap;
    }
}
