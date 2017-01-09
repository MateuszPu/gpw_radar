package com.gpw.radar.repository.stock;

import com.gpw.radar.config.CacheConfiguration;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockDetails;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockDetailsRepository extends JpaRepository<StockDetails, String> {

    @Cacheable(value = CacheConfiguration.STOCK_DETAILS_BY_TICKER_CACHE)
    Page<StockDetails> findByStockTickerOrderByDateDesc(String ticker, Pageable pageable);

    Page<StockDetails> findByStockOrderByDateDesc(Stock stock, Pageable pageable);


}
