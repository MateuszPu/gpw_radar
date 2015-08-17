package com.gpw.radar.repository;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.gpw.radar.domain.Stock;
import com.gpw.radar.domain.StockDetails;
import com.gpw.radar.domain.enumeration.Tickers;

/**
 * Spring Data JPA repository for the StockDetails entity.
 */
public interface StockDetailsRepository extends JpaRepository<StockDetails,Long> {
	List<StockDetails> findByStockTickerOrderByDateDesc(Tickers ticker);
	List<StockDetails> findByStockTickerOrderByDateAsc(Tickers ticker);
    Page<StockDetails> findByStockTickerOrderByDateDesc(Tickers ticker, Pageable pageable);
	StockDetails findTopByOrderByDateDesc();
	StockDetails findTopByStockOrderByDateDesc(Stock stock);
	List<StockDetails> findByStockTicker(Tickers ticker);
	List<StockDetails> findByStock(Stock stock);
	Page<StockDetails> findByStockAndDateBeforeOrderByDateDesc(Stock stock, LocalDate date, Pageable pageable);
	Page<StockDetails> findByStockOrderByDateDesc(Stock stock, Pageable pageable);
}