package com.gpw.radar.dao.newsmessage;

import com.gpw.radar.config.CacheConfiguration;
import com.gpw.radar.domain.rss.NewsMessage;
import com.gpw.radar.rabbitmq.consumer.rss.news.RssType;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface NewsMessageDAO {

    @Cacheable(cacheNames = CacheConfiguration.RSS_NEWS_BY_TYPE_CACHE, key = "#p0")
    List<NewsMessage> findTop5ByTypeOrderByNewsDateTimeDesc(RssType type);

    List<NewsMessage> findByTypeAndNewsDateTimeAfterAndNewsDateTimeBefore(RssType type, LocalDateTime startDate, LocalDateTime endDate);

    @CacheEvict(cacheNames = {CacheConfiguration.RSS_NEWS_BY_TYPE_CACHE}, beforeInvocation = true, key = "#p0.type")
    NewsMessage save(NewsMessage news);

    Iterable<NewsMessage> save(Iterable<NewsMessage> newsMessages);

    Set<NewsMessage> findTop5ByOrderByNewsDateTimeDesc();


}
