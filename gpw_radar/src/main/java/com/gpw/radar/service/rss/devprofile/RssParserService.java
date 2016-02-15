package com.gpw.radar.service.rss.devprofile;

import com.gpw.radar.config.Constants;
import com.gpw.radar.domain.rss.NewsMessage;
import com.gpw.radar.service.chat.RssObserver;
import com.gpw.radar.service.rss.RssObservable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("!" + Constants.SPRING_PROFILE_PRODUCTION)
public class RssParserService implements RssObservable {

    @Override
    public void addRssObserver(RssObserver observer) {

    }

    @Override
    public void removeRssObserver(RssObserver observer) {

    }

    @Override
    public void notifyRssObservers(List<NewsMessage> parsedRssNewsMessage) {

    }

    @Override
    public void fireCron() {
    }
}
