package com.gpw.radar.repository;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.gpw.radar.domain.Stock;
import com.gpw.radar.domain.StockDetails;
import com.gpw.radar.domain.enumeration.Ticker;

public interface StockDetailsRepository extends JpaRepository<StockDetails,Long> {
	List<StockDetails> findByStockTickerOrderByDateDesc(Ticker ticker);
	List<StockDetails> findByStockTickerOrderByDateAsc(Ticker ticker);
    Page<StockDetails> findByStockTickerOrderByDateDesc(Ticker ticker, Pageable pageable);
	StockDetails findTopByOrderByDateDesc();
	StockDetails findTopByStockOrderByDateDesc(Stock stock);
	List<StockDetails> findByStockTicker(Ticker ticker);
	List<StockDetails> findByStock(Stock stock);
	Page<StockDetails> findByStockAndDateBeforeOrderByDateDesc(Stock stock, LocalDate date, Pageable pageable);
	Page<StockDetails> findByStockOrderByDateDesc(Stock stock, Pageable pageable);
}