package com.gpw.radar.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.cache.jcache.JCacheCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableCaching
@AutoConfigureAfter(value = {MetricsConfiguration.class, DatabaseConfiguration.class})
@Profile("!" + Constants.SPRING_PROFILE_FAST)
public class CacheConfiguration {

    private final Logger log = LoggerFactory.getLogger(CacheConfiguration.class);
    public static final String STOCK_DETAILS_BY_TICKER_CACHE = "stockDetailsByTickerCache";
    public static final String RSS_NEWS_BY_TYPE_CACHE = "rssNewsByTypeCache";
    public static final String ALL_STOCKS_FETCH_INDICATORS_CACHE = "allStocksFetchIndicatorsCache";
    public static final String STOCKS_FOLLOWED_BY_USER_CACHE = "stocksFollowedByUserCache";
    public static final String TRENDING_STOCKS_CACHE = "trendingStocksCache";
    public static final String USER_DETAILS_CACHE = "userDetailsCache";

    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        List<String> caches = new ArrayList<>();
        caches.add(STOCK_DETAILS_BY_TICKER_CACHE);
        caches.add(RSS_NEWS_BY_TYPE_CACHE);
        caches.add(ALL_STOCKS_FETCH_INDICATORS_CACHE);
        caches.add(STOCKS_FOLLOWED_BY_USER_CACHE);
        caches.add(TRENDING_STOCKS_CACHE);
        caches.add(USER_DETAILS_CACHE);
        cacheManager.setCacheNames(caches);
        return cacheManager;
    }
}
