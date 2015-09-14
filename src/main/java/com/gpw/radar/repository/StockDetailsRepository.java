package com.gpw.radar.repository;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gpw.radar.domain.Stock;
import com.gpw.radar.domain.StockDetails;
import com.gpw.radar.domain.enumeration.Ticker;
import com.gpw.radar.service.auto.update.stockDetails.ParserMethod;

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
	
	@Query(value="select * from stock_details_parser_method limit 1", nativeQuery = true)
	ParserMethod findMethod();
	
	@Modifying
	@Query(value="update stock_details_parser_method set method = :method", nativeQuery = true)
	void setStockDetailsParserMethod(@Param("method") String method); 
}