package com.gpw.radar.repository.stock;

import com.gpw.radar.config.CacheConfiguration;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockDetails;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface StockDetailsRepository extends JpaRepository<StockDetails, String> {
    List<StockDetails> findByStockTickerOrderByDateDesc(String ticker);

    List<StockDetails> findByStockTickerOrderByDateAsc(String ticker);

    @Cacheable(value = CacheConfiguration.STOCK_DETAILS_BY_TICKER_CACHE)
    Page<StockDetails> findByStockTickerOrderByDateDesc(String ticker, Pageable pageable);

    StockDetails findTopByOrderByDateDesc();

    @Query(value = "select date from stock_details order by date desc limit 1", nativeQuery = true)
    @Cacheable(cacheNames = CacheConfiguration.LAST_QUTED_DATE)
    LocalDate findTopDate();

    StockDetails findTopByStockOrderByDateDesc(Stock stock);

    List<StockDetails> findByStockTicker(String ticker);

    List<StockDetails> findByStock(Stock stock);

    Page<StockDetails> findByStockAndDateBeforeOrderByDateDesc(Stock stock, LocalDate date, Pageable pageable);

    Page<StockDetails> findByStockOrderByDateDesc(Stock stock, Pageable pageable);
}
