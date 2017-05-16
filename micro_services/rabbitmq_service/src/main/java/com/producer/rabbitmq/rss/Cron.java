package com.producer.rabbitmq.rss;

import com.producer.rabbitmq.service.JsonConverter;
import com.rss.parser.RssParser;
import com.rss.parser.model.GpwNews;
import com.producer.rabbitmq.config.rss.RssType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Comparator;
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

	@Scheduled(cron = "*/3 * 8-18 * * MON-FRI")
	@Scheduled(cron = "0 */5 19-23 * * MON-FRI")
	@Scheduled(cron = "0 */30 0-7 * * MON-FRI")
	@Scheduled(cron = "0 */30 * * * SAT,SUN")
	public void fireCron() {
		for (RssType rss : rssTypeTimeMap.keySet()) {
			LOGGER.info("Run cron for {}", rss);
			RssParser parser = rssTypeParserMap.get(rss);
			List<GpwNews> gpwNewses = parser.parseBy(this.rssTypeTimeMap.get(rss));
			if (!gpwNewses.isEmpty()) {
				logInvalidLinks(gpwNewses, rss);
				LocalDateTime dateTime = gpwNewses.stream()
						.max(Comparator.comparing(GpwNews::getNewsDateTime))
						.get()
						.getNewsDateTime();
				rssTypeTimeMap.put(rss, dateTime);
				String json = jsonConverter.convertToJson(gpwNewses);
				producer.publish(json, rss.name());
			}
		}
	}

	private void logInvalidLinks(List<GpwNews> gpwNewses, RssType rss) {
		for (GpwNews news : gpwNewses) {
			if (checkStatusOfLink(news.getLink()) != 200) {
				LOGGER.warn("News type \"{}\" on date \"{}\" has not valid link \"{}\"",
						rss, news.getNewsDateTime(), news.getLink());
			}
		}
	}

	private int checkStatusOfLink(String link) {
		int httpStatusCode = 400;
		HttpURLConnection connection = null;
		try {
			URL url = new URL(link);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			httpStatusCode = connection.getResponseCode();
		} catch (IOException e) {
			LOGGER.error("Exception in {} with clause : {}", this.getClass().getName(), e.getCause());
		} finally {
			connection.disconnect();
		}
		return httpStatusCode;
	}
}