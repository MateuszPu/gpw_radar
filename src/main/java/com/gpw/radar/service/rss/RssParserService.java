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
import com.gpw.radar.domain.enumeration.RssType;
import com.gpw.radar.domain.rss.StockNewsMessage;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.repository.UserRepository;
import com.gpw.radar.repository.chat.ChatMessageRepository;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.service.MailService;
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
	private MailService mailService;

	@Inject
	private SimpMessagingTemplate template;

	private Map<RssType, LocalDateTime> rssLinks;

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
		for (Map.Entry<RssType, LocalDateTime> entry : rssLinks.entrySet()) {
			getNewsMessagesFromUrl(entry.getKey(), entry.getValue());
		}
	}

	private void getNewsMessagesFromUrl(RssType rssType, LocalDateTime date) {
		SyndFeed feed = null;
		try {
			feed = feedFetcher.retrieveFeed(new URL(rssType.getUrl()));
			List<SyndEntry> syndFeedItems = feed.getEntries();

			LocalDateTime dt = new LocalDateTime(syndFeedItems.get(0).getPublishedDate());

			for (Object syndFeedEntry : syndFeedItems) {
				SyndEntry syndEntry = (SyndEntry) syndFeedEntry;
				LocalDateTime syndEntryPublishDate = new LocalDateTime(syndEntry.getPublishedDate());
				if (syndEntryPublishDate.isAfter(date)) {
					StockNewsMessage message = parseMessageFrom(syndEntry, rssType);
					if(message.getStock() != null){
						mailService.informUserAboutStockNewsByEmail(message);
					}
					template.convertAndSend("/webchat/recive", (ChatMessage) message);
				} else {
					break;
				}
				rssLinks.put(rssType, dt);
			}
		} catch (IllegalArgumentException | IOException | FeedException | FetcherException e) {
			logger.error("error occurs", e.getMessage());
		}
	}

	public StockNewsMessage parseMessageFrom(SyndEntry syndEntry, RssType type) {
		String message = syndEntry.getTitle();
		String link = syndEntry.getLink();
		Date date = syndEntry.getPublishedDate();

		StockNewsMessage stNwMsg = new StockNewsMessage();
		stNwMsg.setType(type);
		stNwMsg.setMessage(message);
		stNwMsg.setLink(link);
		stNwMsg.setCreatedDate(new DateTime(date));
		stNwMsg.setStock(getStockFromTitle(message));
		stNwMsg.setUserLogin("system");
		stNwMsg.setUser(userRepository.findOneByLogin("system").get());
		chatMessageRepository.save(stNwMsg);
		return stNwMsg;
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
		rssLinks = new HashMap<RssType, LocalDateTime>();
		rssLinks.put(RssType.CHALLENGE, new LocalDateTime());
		rssLinks.put(RssType.EBI, new LocalDateTime(2014,1,1,1,1));
		rssLinks.put(RssType.ESPI, new LocalDateTime(2014,1,1,1,1));
		rssLinks.put(RssType.PAP, new LocalDateTime());
		rssLinks.put(RssType.RECOMMENDATIONS, new LocalDateTime());
		rssLinks.put(RssType.RESULTS, new LocalDateTime());
	}
}
