package com.rss.parser;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.rss.parser.model.GpwNews;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

public class Parser implements RssParser {

    private final URL url;

    private Parser() {
        throw new IllegalStateException("Cannot create parser without url");
    }

    public Parser(String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("s");
        }
    }

    @Override
    public List<GpwNews> parseBy(LocalDateTime dateTime) {
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = null;
        try {
            feed = input.build(new XmlReader(url));
        } catch (FeedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<GpwNews> newses = getEntriesAfter(feed, dateTime).stream().map(e -> getNewsFrom(e)).collect(Collectors.toList());
        return newses;
    }

    private List<SyndEntry> getEntriesAfter(SyndFeed feed, LocalDateTime dateTime) {
        return feed.getEntries().stream().filter(e -> getDateFrom(e).isAfter(dateTime)).collect(Collectors.toList());
    }

    private GpwNews getNewsFrom(SyndEntry syndEntry) {
        LocalDateTime date = getDateFrom(syndEntry);
        String message = syndEntry.getTitle();
        String link = syndEntry.getLink();
        return new GpwNews(date, message, link);
    }

    private LocalDateTime getDateFrom(SyndEntry syndEntry) {
        return LocalDateTime.ofInstant(syndEntry.getPublishedDate().toInstant(), ZoneId.systemDefault());
    }
}
