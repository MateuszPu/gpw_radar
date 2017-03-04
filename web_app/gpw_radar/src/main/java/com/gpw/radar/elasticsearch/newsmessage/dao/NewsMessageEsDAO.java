package com.gpw.radar.elasticsearch.newsmessage.dao;

import com.gpw.radar.dao.newsmessage.NewsMessageDAO;
import com.gpw.radar.domain.rss.NewsMessage;
import com.gpw.radar.rabbitmq.consumer.rss.news.RssType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class NewsMessageEsDAO implements NewsMessageDAO {

    @Override
    public List<NewsMessage> findTop5ByTypeOrderByNewsDateTimeDesc(RssType type) {
        return null;
    }

    @Override
    public List<NewsMessage> findByTypeAndNewsDateTimeAfterAndNewsDateTimeBefore(RssType type, LocalDateTime startDate, LocalDateTime endDate) {
        return null;
    }

    @Override
    public NewsMessage save(NewsMessage news) {
        return null;
    }

    @Override
    public Iterable<NewsMessage> save(Iterable<NewsMessage> newsMessages) {
        return null;
    }

    @Override
    public Set<NewsMessage> findTop5ByOrderByNewsDateTimeDesc() {
        return null;
    }
}
