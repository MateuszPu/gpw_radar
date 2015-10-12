package com.gpw.radar.service.rss;

import java.io.IOException;
import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.gpw.radar.domain.rss.StockNewsMessage;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.web.sockets.ChatController;
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
	private ChatController chatController;
	
	private LocalDate dateOfLastRssMessage = new LocalDate(2014, 1, 1);

	public List<StockNewsMessage> parseMessagesFromRssUrl(String url) {
		List<StockNewsMessage> stockNewsMessages = new ArrayList<StockNewsMessage>();
		SyndFeed feed = null;
		try {
			feed = feedFetcher.retrieveFeed(new URL(url));
		} catch (IllegalArgumentException | IOException | FeedException | FetcherException e) {
			logger.error("error occurs", e.getMessage());
		}
		List<SyndEntry> syndFeedItems = feed.getEntries();
		for (Object syndFeedEntry : syndFeedItems) {
			SyndEntry syndEntry = (SyndEntry) syndFeedEntry;
			String message = syndEntry.getTitle();
			String link = syndEntry.getLink();
			Date date = syndEntry.getPublishedDate();
			
			StockNewsMessage stNwMsg = new StockNewsMessage();
			stNwMsg.setMessage(message);
			stNwMsg.setLink(link);
			stNwMsg.setCreatedDate(new DateTime(date));
			stNwMsg.setStock(getStockFromTitle(message));
			chatController.sendSystemMessage(stNwMsg);
		}

		return stockNewsMessages;
	}
	
	@Scheduled(cron="*/5 * * * * ?")
	public void tat() {
		SyndFeed feed = null;
		try {
			feed = feedFetcher.retrieveFeed(new URL("http://biznes.pap.pl/pl/rss/6614"));
			LocalDate dt = new LocalDate(feed.getEntries().get(0).getPublishedDate());
			if(!dt.equals(dateOfLastRssMessage)){
				parseMessagesFromRssUrl("http://biznes.pap.pl/pl/rss/6614");
			}
			dateOfLastRssMessage = dt;
		} catch (IllegalArgumentException | IOException | FeedException | FetcherException e) {
			logger.error("error occurs", e.getMessage());
		}
	}

	private Stock getStockFromTitle(String message) {
		Pattern pattern = Pattern.compile("([0-9A-Z-/.]{2,} *)*");
		Matcher matcher = pattern.matcher(message);
		if (matcher.find()) {
			return stockRepository.findByStockName(matcher.group(0).trim());
		}
		return null;
	}
}
