//package com.rss.parser;
//
//import com.rometools.fetcher.FetcherException;
//import com.rometools.rome.feed.synd.SyndEntry;
//import com.rometools.rome.feed.synd.SyndFeed;
//import com.rometools.rome.io.FeedException;
//import gpw.rss.reader.Sender;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//import java.io.IOException;
//import java.net.URL;
//import java.time.Instant;
//import java.time.LocalDateTime;
//import java.time.ZoneOffset;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class Parser {
//
//    private final Sender sender;
//    private Map<RssType, LocalDateTime> rssLinks;
//
//    @Autowired
//    public Parser(Sender sender) {
//        this.sender = sender;
//    }
//
//    @PostConstruct
//    private void prepareMapWithLinks() {
//        rssLinks = new HashMap<>();
//        LocalDateTime date = LocalDateTime.now();
//        rssLinks.put(RssType.CHALLENGE, date);
//        rssLinks.put(RssType.EBI, date);
//        rssLinks.put(RssType.ESPI, date);
//        rssLinks.put(RssType.PAP, date);
//        rssLinks.put(RssType.RECOMMENDATIONS, date);
//        rssLinks.put(RssType.RESULTS, date);
//    }
//
//    @Scheduled(cron = "*/5 * 8-17 * * MON-FRI")
//    @Scheduled(cron = "0 */5 18-23 * * MON-FRI")
//    @Scheduled(cron = "0 */30 0-7 * * MON-FRI")
//    @Scheduled(cron = "0 */30 * * * SAT,SUN")
//    public void fireCron() {
//        List<String> parsedRssNewsMessage = new ArrayList<>();
//        if (isNewRssMessagePresented(parsedRssNewsMessage)) {
//            notifyRssObservers(parsedRssNewsMessage);
//        }
//    }
//
//    private boolean isNewRssMessagePresented(List<String> parsedRssNewsMessage) {
//        for (Map.Entry<RssType, LocalDateTime> entry : rssLinks.entrySet()) {
//            parsedRssNewsMessage.addAll(getNewsMessagesFromUrl(entry.getKey(), entry.getValue()));
//        }
//        return !parsedRssNewsMessage.isEmpty();
//    }
//
//    private List<NewsMessage> getNewsMessagesFromUrl(RssType rssType, LocalDateTime date) {
//        SyndFeed feed;
//        List<NewsMessage> rssNewsMessages = new ArrayList<>();
//        try {
//            feed = feedFetcher.retrieveFeed(new URL(rssType.getUrl()));
//            List<SyndEntry> syndFeedItems = feed.getEntries();
//
//            int indexOfLatestItem = 0;
//            Instant instant = Instant.ofEpochMilli(syndFeedItems.get(indexOfLatestItem).getPublishedDate().getTime());
//            LocalDateTime dt = LocalDateTime.ofInstant(instant, ZoneOffset.systemDefault());
//
//            for (Object syndFeedEntry : syndFeedItems) {
//                SyndEntry syndEntry = (SyndEntry) syndFeedEntry;
//                Instant inst = Instant.ofEpochMilli(syndFeedItems.get(indexOfLatestItem).getPublishedDate().getTime());
//                LocalDateTime syndEntryPublishDate = LocalDateTime.ofInstant(inst, ZoneOffset.systemDefault());
//                if (syndEntryPublishDate.isAfter(date)) {
//                    NewsMessage message = parseNewsMessageFromRssChannel(syndEntry, rssType);
//                    rssNewsMessages.add(message);
//                } else {
//                    break;
//                }
//                rssLinks.put(rssType, dt);
//            }
//        } catch (IllegalArgumentException | IOException | FeedException | FetcherException e) {
//            logger.error("error occurs", e.getMessage());
//        }
//
//        return rssNewsMessages;
//    }
//
//}
