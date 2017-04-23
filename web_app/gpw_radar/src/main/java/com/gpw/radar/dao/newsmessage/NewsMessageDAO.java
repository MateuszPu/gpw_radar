package com.gpw.radar.dao.newsmessage;

import com.gpw.radar.rabbitmq.consumer.rss.news.RssType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface NewsMessageDAO<T> {

    List<T> findTop5ByTypeOrderByNewsDateTimeDesc(RssType type);

    List<T> findByTypeAndNewsDateTimeAfterAndNewsDateTimeBefore(RssType type, LocalDateTime startDate, LocalDateTime endDate);

    T save(T news);

    Iterable<T> save(Iterable<T> newsMessages);

    List<T> findTop5ByOrderByNewsDateTimeDesc();

}
