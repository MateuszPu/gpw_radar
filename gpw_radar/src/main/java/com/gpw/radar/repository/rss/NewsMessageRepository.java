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
import java.util.Set;

public interface NewsMessageRepository extends JpaRepository<NewsMessage, Long> {


    //do not use this to pageable result, as the cache will break it, it is use it only for first five news by type
    @Cacheable(cacheNames = CacheConfiguration.RSS_NEWS_BY_TYPE_CACHE, key = "#p0")
    Page<NewsMessage> findByType(RssType type, Pageable pageable);

    List<NewsMessage> findByTypeAndCreatedDateAfterAndCreatedDateBefore(RssType type, ZonedDateTime startDate, ZonedDateTime endDate);

    @CacheEvict(cacheNames = {CacheConfiguration.RSS_NEWS_BY_TYPE_CACHE}, beforeInvocation = true, key = "#p0.type")
    NewsMessage save(NewsMessage news);

    Set<NewsMessage> findTop5ByOrderByCreatedDateDesc();
}
