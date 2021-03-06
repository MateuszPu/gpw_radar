package com.gpw.radar.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
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
    public static final String TRENDING_STOCKS_CACHE = "trendingStocksCache";
    public static final String STOCK_TICKERS_CACHE = "stockTickersCache";
    public static final String ALL_STOCK_FINANCE_EVENTS_CACHE = "allStocksFinanceEventsCache";
    public static final String RSS_NEWS_BY_TYPE_AND_DATE_CACHE = "rssNewsByTypeAndDateCache";
    public static final String USER_INFO_CACHE = " userInfoCache";
    public static final String STOCK_CACHE = " stockCache";

    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        List<String> caches = new ArrayList<>();
        caches.add(STOCK_DETAILS_BY_TICKER_CACHE);
        caches.add(RSS_NEWS_BY_TYPE_CACHE);
        caches.add(ALL_STOCKS_FETCH_INDICATORS_CACHE);
        caches.add(TRENDING_STOCKS_CACHE);
        caches.add(STOCK_TICKERS_CACHE);
        caches.add(ALL_STOCK_FINANCE_EVENTS_CACHE);
        caches.add(USER_INFO_CACHE);
        caches.add(STOCK_CACHE);
        cacheManager.setCacheNames(caches);
        return cacheManager;
    }
}
