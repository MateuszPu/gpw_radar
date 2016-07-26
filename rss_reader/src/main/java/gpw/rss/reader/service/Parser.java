package gpw.rss.reader.service;

import com.rometools.fetcher.FeedFetcher;
import com.rometools.fetcher.impl.HttpURLFeedFetcher;
import gpw.rss.reader.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class Parser {

    private final Sender sender;
    private Map<RssType, LocalDateTime> rssLinks;

    @Autowired
    public Parser(Sender sender) {
        this.sender = sender;
    }

    @PostConstruct
    private void prepareMapWithLinks() {
        rssLinks = new HashMap<>();
        LocalDateTime date = LocalDateTime.now();
        rssLinks.put(RssType.CHALLENGE, date);
        rssLinks.put(RssType.EBI, date);
        rssLinks.put(RssType.ESPI, date);
        rssLinks.put(RssType.PAP, date);
        rssLinks.put(RssType.RECOMMENDATIONS, date);
        rssLinks.put(RssType.RESULTS, date);
    }
}
