package com.rss.rabbitmq.cron;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rss.parser.Parser;
import com.rss.parser.model.GpwNews;
import com.rss.rabbitmq.sender.Sender;
import com.rss.rabbitmq.config.RssType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class Cron {

    @Resource(name = "rssTypesWithLink")
    private Map<RssType, LocalDateTime> rssTypes;

    private final Sender sender;

    @Autowired
    public Cron(Sender sender) {
        this.sender = sender;
    }

    @Scheduled(cron = "*/5 * 8-23 * * MON-FRI")
//    @Scheduled(cron = "0 */5 18-23 * * MON-FRI")
    @Scheduled(cron = "0 */30 0-7 * * MON-FRI")
    @Scheduled(cron = "0 */30 * * * SAT,SUN")
    public void fireCron() {
        Map<RssType, Parser> typeParserMap = rssTypes.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> new Parser(e.getKey().getUrl())));

        for (RssType rss : rssTypes.keySet()) {
            Parser parser = typeParserMap.get(rss);
            List<GpwNews> gpwNewses = parser.parseBy(this.rssTypes.get(rss));
            if (!gpwNewses.isEmpty()) {
                LocalDateTime dateTime = gpwNewses.stream().max((e1, e2) -> e1.getTimeNews().compareTo(e2.getTimeNews())).get().getTimeNews();
                rssTypes.put(rss, dateTime);
                sender.send(convertToJson(gpwNewses), rss.name());
            }
        }
    }

    private String convertToJson(List<GpwNews> gpwNewses) {
        ObjectMapper mapper = new ObjectMapper();
        String result = null;
        try {
            result = mapper.writeValueAsString(gpwNewses);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
