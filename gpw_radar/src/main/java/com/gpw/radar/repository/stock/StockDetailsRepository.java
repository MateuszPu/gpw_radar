package com.gpw.radar.repository.stock;

import com.gpw.radar.domain.enumeration.StockTicker;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockDetails;
import org.joda.time.LocalDate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockDetailsRepository extends JpaRepository<StockDetails, Long> {
    List<StockDetails> findByStockTickerOrderByDateDesc(StockTicker ticker);

    List<StockDetails> findByStockTickerOrderByDateAsc(StockTicker ticker);

    @Cacheable(value = "stockDetailsByTickerCache")
    Page<StockDetails> findByStockTickerOrderByDateDesc(StockTicker ticker, Pageable pageable);

    StockDetails findTopByOrderByDateDesc();

    StockDetails findTopByStockOrderByDateDesc(Stock stock);

    List<StockDetails> findByStockTicker(StockTicker ticker);

    List<StockDetails> findByStock(Stock stock);

    Page<StockDetails> findByStockAndDateBeforeOrderByDateDesc(Stock stock, LocalDate date, Pageable pageable);

    Page<StockDetails> findByStockOrderByDateDesc(Stock stock, Pageable pageable);
}
