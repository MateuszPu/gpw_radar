package com.rss.rabbitmq.cron;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.rss.parser.Parser;
import com.rss.parser.RssParser;
import com.rss.parser.model.GpwNews;
import com.rss.rabbitmq.config.RssType;
import com.rss.rabbitmq.sender.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class Cron {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "rssTypeTimeMap")
    private Map<RssType, LocalDateTime> rssTypeTimeMap;

    @Resource(name = "rssTypeParserMap")
    private Map<RssType, RssParser> rssTypeParserMap;

    private final Sender sender;

    @Autowired
    public Cron(Sender sender) {
        this.sender = sender;
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
                sender.send(convertToJson(gpwNewses), rss.name());
            }
        }
    }

    private String convertToJson(List<GpwNews> gpwNewses) {
        ObjectMapper mapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME));
        mapper.registerModule(javaTimeModule);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        String result = null;
        try {
            result = mapper.writeValueAsString(gpwNewses);
        } catch (JsonProcessingException e) {
            LOGGER.error("Exception in "
                    + this.getClass().getName()
                    + " with clause : "
                    + e.getCause());
        }
        return result;
    }
}