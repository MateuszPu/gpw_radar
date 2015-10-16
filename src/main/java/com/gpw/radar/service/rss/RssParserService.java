package com.gpw.radar.service.rss;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.gpw.radar.domain.chat.ChatMessage;
import com.gpw.radar.domain.rss.StockNewsMessage;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.repository.UserRepository;
import com.gpw.radar.repository.chat.ChatMessageRepository;
import com.gpw.radar.repository.stock.StockRepository;
import com.rometools.fetcher.FeedFetcher;
import com.rometools.fetcher.FetcherException;
import com.rometools.fetcher.impl.HttpURLFeedFetcher;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;

@Service
public class RssParserService {

	private final Logger logger = LoggerFactory.getLogger(RssParserService.class);
	private FeedFetcher feedFetcher = new HttpURLFeedFetcher();

	@Inject
	private StockRepository stockRepository;

	@Inject
	private ChatMessageRepository chatMessageRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private SimpMessagingTemplate template;
	
	private final String RESULTS = "http://biznes.pap.pl/pl/rss/6639";
	private final String CHALLENGE = "http://biznes.pap.pl/pl/rss/6638";
	private final String RECOMMENDATIONS = "http://biznes.pap.pl/pl/rss/6634";
	private final String PAP = "http://biznes.pap.pl/pl/rss/6608";
	private final String EBI = "http://biznes.pap.pl/pl/rss/6612";
	private final String ESPI = "http://biznes.pap.pl/pl/rss/6614";
	private Map<String, LocalDateTime> rssLinks;
	
	@Scheduled(cron = "*/3 * 8-17 * * MON-FRI")
	public void fireDuringWeekDuringGpwSession() {
		runRssParser();
	}
	
	@Scheduled(cron = "0 */5 18-23 * * MON-FRI")
	public void fireDuringWeekToMidnghit() {
		runRssParser();
	}
	
	@Scheduled(cron = "0 */15 0-7 * * MON-FRI")
	public void fireDuringWeek() {
		runRssParser();
	}
	
	@Scheduled(cron = "0 */15 * * * SAT,SUN")
	public void fireDuringWeekend() {
		runRssParser();
	}
	
	private void runRssParser() {
		for (Map.Entry<String, LocalDateTime> entry : rssLinks.entrySet()) {
			getMessagesFromUrl(entry.getKey(), entry.getValue());
		}
	}

	private void getMessagesFromUrl(String url, LocalDateTime date) {
		SyndFeed feed = null;
		try {
			feed = feedFetcher.retrieveFeed(new URL(url));
			List<SyndEntry> syndFeedItems = feed.getEntries();

			LocalDateTime dt = new LocalDateTime(syndFeedItems.get(0).getPublishedDate());

			for (Object syndFeedEntry : syndFeedItems) {
				SyndEntry syndEntry = (SyndEntry) syndFeedEntry;
				LocalDateTime syndEntryPublishDate = new LocalDateTime(syndEntry.getPublishedDate());
				if (syndEntryPublishDate.isAfter(date)) {
					parseMessageFrom(syndEntry);
				} 
				else {
					break;
				}
				rssLinks.put(url, dt);
			}
		} catch (IllegalArgumentException | IOException | FeedException | FetcherException e) {
			logger.error("error occurs", e.getMessage());
		}
	}

	public void parseMessageFrom(SyndEntry syndEntry) {
		String message = syndEntry.getTitle();
		String link = syndEntry.getLink();
		Date date = syndEntry.getPublishedDate();

		StockNewsMessage stNwMsg = new StockNewsMessage();
		stNwMsg.setMessage(message);
		stNwMsg.setLink(link);
		stNwMsg.setCreatedDate(new DateTime(date));
		stNwMsg.setStock(getStockFromTitle(message));
		stNwMsg.setUserLogin("system");
		stNwMsg.setUser(userRepository.findOneByLogin("system").get());
		chatMessageRepository.save(stNwMsg);
		template.convertAndSend("/webchat/recive", (ChatMessage) stNwMsg);
	}

	private Stock getStockFromTitle(String message) {
		Pattern pattern = Pattern.compile("([£•” åØè∆—0-9A-Z-/.] *)*");
		Matcher matcher = pattern.matcher(message);
		if (matcher.find()) {
			return stockRepository.findByStockName(matcher.group(0).substring(0, matcher.group(0).length() - 1).trim());
		}
		return null;
	}
	
	@PostConstruct
	private void prepareMapWithLinks() {
		rssLinks = new HashMap<String, LocalDateTime>();
		rssLinks.put(CHALLENGE, new LocalDateTime());
		rssLinks.put(EBI, new LocalDateTime());
		rssLinks.put(ESPI, new LocalDateTime());
		rssLinks.put(PAP, new LocalDateTime());
		rssLinks.put(RECOMMENDATIONS, new LocalDateTime());
		rssLinks.put(RESULTS, new LocalDateTime());
	}
}
