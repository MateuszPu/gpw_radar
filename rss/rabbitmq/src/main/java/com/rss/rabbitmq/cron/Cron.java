package com.rss.rabbitmq.cron;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.rss.parser.Parser;
import com.rss.parser.model.GpwNews;
import com.rss.rabbitmq.config.RssType;
import com.rss.rabbitmq.sender.Sender;
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

    @Resource(name = "rssTypeTimeMap")
    private Map<RssType, LocalDateTime> rssTypeTimeMap;

    @Resource(name = "rssTypeParserMap")
    Map<RssType, Parser> rssTypeParserMap;

    private final Sender sender;

    @Autowired
    public Cron(Sender sender) {
        this.sender = sender;
    }

    @Scheduled(cron = "*/5 * 8-17 * * MON-FRI")
    @Scheduled(cron = "0 */5 18-23 * * MON-FRI")
    @Scheduled(cron = "0 */30 0-7 * * MON-FRI")
    @Scheduled(cron = "0 */30 * * * SAT,SUN")
    public void fireCron() {
        for (RssType rss : rssTypeTimeMap.keySet()) {
            Parser parser = rssTypeParserMap.get(rss);
            List<GpwNews> gpwNewses = parser.parseBy(this.rssTypeTimeMap.get(rss));
            if (!gpwNewses.isEmpty()) {
                LocalDateTime dateTime = gpwNewses.stream().max((e1, e2) -> e1.getTimeNews().compareTo(e2.getTimeNews())).get().getTimeNews();
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
            e.printStackTrace();
        }
        return result;
    }
}