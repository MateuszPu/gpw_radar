package com.gpw.radar.service.chat;

import com.gpw.radar.domain.rss.NewsMessage;

import java.util.List;

public interface RssObserver {
    void updateRssNewsMessage(List<NewsMessage> parsedRssNewsMessage);
}
