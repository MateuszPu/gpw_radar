package com.gpw.radar.repository.rss;

import com.gpw.radar.config.CacheConfiguration;
import com.gpw.radar.domain.enumeration.RssType;
import com.gpw.radar.domain.rss.NewsMessage;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;

public interface NewsMessageRepository extends JpaRepository<NewsMessage, Long> {


    //do not use this to pageable result, as the cache will break it
    //TODO: consider to refactor
    @Cacheable(cacheNames = CacheConfiguration.RSS_NEWS_BY_TYPE_CACHE, key = "#p0")
    Page<NewsMessage> findByType(RssType type, Pageable pageable);

    List<NewsMessage> findByTypeAndCreatedDateAfterAndCreatedDateBefore(RssType type, ZonedDateTime startDate, ZonedDateTime endDate);

    @CacheEvict(cacheNames = {CacheConfiguration.RSS_NEWS_BY_TYPE_CACHE}, beforeInvocation = true, key = "#p0.type")
    NewsMessage save(NewsMessage news);
}
