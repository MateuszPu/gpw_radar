package com.rss.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Optional;
import java.util.stream.Collectors;

public class Parser implements RssParser {

    private final URL url;

    private Parser() {
        throw new IllegalStateException("Cannot create parser without url");
    }

    public Parser(String url) throws MalformedURLException {
        this.url = new URL(url);
    }

    @Override
    public Optional<String> parseFrom(LocalDateTime dateTime) throws IOException, FeedException {
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(url));
        List<GpwNews> newses = getEntriesAfter(feed, dateTime).stream().map(e -> getNewsFrom(e)).collect(Collectors.toList());
        return toJson(newses);
    }

    private Optional<String> toJson(List<GpwNews> newses) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return Optional.of(mapper.writeValueAsString(newses));
    }

    private List<SyndEntry> getEntriesAfter(SyndFeed feed, LocalDateTime dateTime) {
        return feed.getEntries().stream().filter(e -> getDateFrom(e).isAfter(dateTime)).collect(Collectors.toList());
    }

    private GpwNews getNewsFrom(SyndEntry syndEntry) {
        LocalDateTime date = getDateFrom(syndEntry);
        String message = syndEntry.getTitle();
        String link = syndEntry.getLink();

        GpwNews parsedNews = new GpwNews(date, message, link);
        return parsedNews;
    }

    private LocalDateTime getDateFrom(SyndEntry syndEntry) {
        return LocalDateTime.ofInstant(syndEntry.getPublishedDate().toInstant(), ZoneId.systemDefault());
    }
}
