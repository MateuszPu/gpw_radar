package com.rss.rabbitmq.rss;

import com.rss.parser.RssParser;
import com.rss.parser.model.GpwNews;
import com.rss.rabbitmq.config.rss.RssType;
import com.rss.rabbitmq.service.JsonConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service("rssCron")
public class Cron {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "rssTypeTimeMap")
    private Map<RssType, LocalDateTime> rssTypeTimeMap;

    @Resource(name = "rssTypeParserMap")
    private Map<RssType, RssParser> rssTypeParserMap;

    private final Producer producer;
    private final JsonConverter<GpwNews> jsonConverter;

    @Autowired
    public Cron(@Qualifier("rssService") Producer producer, JsonConverter<GpwNews> jsonConverter) {
        this.producer = producer;
        this.jsonConverter = jsonConverter;
    }

    @Scheduled(cron = "*/5 * 8-19 * * MON-FRI")
    @Scheduled(cron = "0 */5 19-23 * * MON-FRI")
    @Scheduled(cron = "0 */30 0-7 * * MON-FRI")
    @Scheduled(cron = "0 */30 * * * SAT,SUN")
    public void fireCron() {
        for (RssType rss : rssTypeTimeMap.keySet()) {
            RssParser parser = rssTypeParserMap.get(rss);
            List<GpwNews> gpwNewses = parser.parseBy(this.rssTypeTimeMap.get(rss));
            if (!gpwNewses.isEmpty()) {
                LocalDateTime dateTime = gpwNewses.stream()
                        .max((e1, e2) -> e1.getNewsDateTime().compareTo(e2.getNewsDateTime()))
                        .get().getNewsDateTime();
                rssTypeTimeMap.put(rss, dateTime);
                String json = jsonConverter.convertToJson(gpwNewses);
                producer.publish(json, rss.name());
            }
        }
    }
}