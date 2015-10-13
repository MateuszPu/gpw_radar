package com.gpw.radar.service.rss;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
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

	private LocalDateTime dateOfLastRssMessage = new LocalDateTime(2014, 1, 1, 12, 12);

	@Scheduled(cron = "*/15 * * * * ?")
	public void tat() {
		getMessagesFromUrl("http://biznes.pap.pl/pl/rss/8659");
//		getMessagesFromUrl("http://biznes.pap.pl/pl/rss/6614");
//		getMessagesFromUrl("http://biznes.pap.pl/pl/rss/6614");
//		getMessagesFromUrl("http://biznes.pap.pl/pl/rss/6614");
		
	}

	private void getMessagesFromUrl(String url) {
		SyndFeed feed = null;
		try {
			feed = feedFetcher.retrieveFeed(new URL(url));
			List<SyndEntry> syndFeedItems = feed.getEntries();

			LocalDateTime dt = new LocalDateTime(syndFeedItems.get(0).getPublishedDate());
			System.out.println("Ostatnia data: " + dateOfLastRssMessage + " data wiadomosci: " + dt);

			for (Object syndFeedEntry : syndFeedItems) {
				SyndEntry syndEntry = (SyndEntry) syndFeedEntry;
				LocalDateTime syndEntryPublishDate = new LocalDateTime(syndEntry.getPublishedDate());
				if (syndEntryPublishDate.isAfter(dateOfLastRssMessage)) {
					parseMessageFrom(syndEntry);
				} 
				else {
					break;
				}
			}
			dateOfLastRssMessage = dt;
		} catch (IllegalArgumentException | IOException | FeedException | FetcherException e) {
			logger.error("error occurs", e.getMessage());
		}
	}

	public void parseMessageFrom(SyndEntry syndEntry) {
		// List<StockNewsMessage> stockNewsMessages = new
		// ArrayList<StockNewsMessage>();
		// SyndFeed feed = null;
		// try {
		// feed = feedFetcher.retrieveFeed(new URL(url));
		// } catch (IllegalArgumentException | IOException | FeedException |
		// FetcherException e) {
		// logger.error("error occurs", e.getMessage());
		// }
		// List<SyndEntry> syndFeedItems = feed.getEntries();
		// for (Object syndFeedEntry : syndFeedItems) {
		// SyndEntry syndEntry = (SyndEntry) syndFeedEntry;
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
}
