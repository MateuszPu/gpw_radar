package com.gpw.radar.service.rss;

import com.gpw.radar.config.Constants;
import com.gpw.radar.domain.enumeration.RssType;
import com.gpw.radar.domain.rss.NewsMessage;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.repository.UserRepository;
import com.gpw.radar.repository.stock.StockRepository;
import com.gpw.radar.service.chat.RssObserver;
import com.rometools.fetcher.FeedFetcher;
import com.rometools.fetcher.FetcherException;
import com.rometools.fetcher.impl.HttpURLFeedFetcher;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Profile(Constants.SPRING_PROFILE_PRODUCTION)
public class RssParserService implements RssObservable {

    private final Logger logger = LoggerFactory.getLogger(RssParserService.class);

    @Inject
    private StockRepository stockRepository;

    @Inject
    private UserRepository userRepository;

    private Map<RssType, LocalDateTime> rssLinks;
    private Set<RssObserver> observers;
    private List<NewsMessage> parsedRssNewsMessage;
    private FeedFetcher feedFetcher;

    @PostConstruct
    private void prepareMapWithLinks() {
        rssLinks = new HashMap<>();
        observers = new HashSet<>();
        feedFetcher = new HttpURLFeedFetcher();

        LocalDateTime date = new LocalDateTime();
        rssLinks.put(RssType.CHALLENGE, new LocalDateTime(date));
        rssLinks.put(RssType.EBI, new LocalDateTime(date));
        rssLinks.put(RssType.ESPI, new LocalDateTime(date));
        rssLinks.put(RssType.PAP, new LocalDateTime(date));
        rssLinks.put(RssType.RECOMMENDATIONS, new LocalDateTime(date));
        rssLinks.put(RssType.RESULTS, new LocalDateTime(date));
    }

    @Override
    @Scheduled(cron = "*/5 * 8-17 * * MON-FRI")
    @Scheduled(cron = "0 */5 18-23 * * MON-FRI")
    @Scheduled(cron = "0 */30 0-7 * * MON-FRI")
    @Scheduled(cron = "0 */30 * * * SAT,SUN")
    public void fireCron() {
        parsedRssNewsMessage = new ArrayList<>();
        if (isNewRssMessagePresented()) {
            notifyRssObservers(parsedRssNewsMessage);
        }
    }

    private boolean isNewRssMessagePresented() {
        for (Map.Entry<RssType, LocalDateTime> entry : rssLinks.entrySet()) {
            parsedRssNewsMessage.addAll(getNewsMessagesFromUrl(entry.getKey(), entry.getValue()));
        }
        return !parsedRssNewsMessage.isEmpty();
    }

    private List<NewsMessage> getNewsMessagesFromUrl(RssType rssType, LocalDateTime date) {
        SyndFeed feed = null;
        List<NewsMessage> rssNewsMessages = new ArrayList<>();
        try {
            feed = feedFetcher.retrieveFeed(new URL(rssType.getUrl()));
            List<SyndEntry> syndFeedItems = feed.getEntries();

            int indexOfLatestItem = 0;
            LocalDateTime dt = new LocalDateTime(syndFeedItems.get(indexOfLatestItem).getPublishedDate());

            for (Object syndFeedEntry : syndFeedItems) {
                SyndEntry syndEntry = (SyndEntry) syndFeedEntry;
                LocalDateTime syndEntryPublishDate = new LocalDateTime(syndEntry.getPublishedDate());
                if (syndEntryPublishDate.isAfter(date)) {
                    NewsMessage message = parseNewsMessageFromRssChannel(syndEntry, rssType);
                    rssNewsMessages.add(message);
                } else {
                    break;
                }
                rssLinks.put(rssType, dt);
            }
        } catch (IllegalArgumentException | IOException | FeedException | FetcherException e) {
            logger.error("error occurs", e.getMessage());
        }

        return rssNewsMessages;
    }

    private NewsMessage parseNewsMessageFromRssChannel(SyndEntry syndEntry, RssType type) {
        String message = syndEntry.getTitle();
        String link = syndEntry.getLink();
        ZonedDateTime date = ZonedDateTime.ofInstant(syndEntry.getPublishedDate().toInstant(), ZoneId.systemDefault());
        NewsMessage stNwMsg = new NewsMessage();
        stNwMsg.setType(type);
        stNwMsg.setMessage(message);
        stNwMsg.setLink(link);
        stNwMsg.setCreatedDate(date);
        stNwMsg.setUser(userRepository.findOneByLogin("system").get());
        if (type.equals(RssType.EBI) || type.equals(RssType.ESPI)) {
            if (getStockFromTitle(message).isPresent()) {
                stNwMsg.setStock(getStockFromTitle(message).get());
            }
        }
        return stNwMsg;
    }

    private Optional<Stock> getStockFromTitle(String message) {
        Pattern pattern = Pattern.compile("^([\\p{javaUpperCase}0-9-/.]+ )+");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            Optional<Stock> stock = stockRepository.findByStockName(matcher.group(0).trim());
            if (stock.isPresent()) {
                return Optional.of(stock.get());
            }
        }
        return Optional.empty();
    }

    @Override
    public void addRssObserver(RssObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeRssObserver(RssObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyRssObservers(List<NewsMessage> parsedRssNewsMessage) {
        observers.forEach(e -> e.updateRssNewsMessage(parsedRssNewsMessage));
    }
}
