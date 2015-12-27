package com.gpw.radar.service.rss;

import com.gpw.radar.domain.rss.NewsMessage;
import com.gpw.radar.service.chat.RssObserver;

import java.util.List;

public interface RssObservable {

    void addRssObserver(RssObserver observer);
    void removeRssObserver(RssObserver observer);
    void notifyRssObservers(List<NewsMessage> parsedRssNewsMessage);
    void fireCron();
}
