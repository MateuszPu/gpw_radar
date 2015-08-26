package com.gpw.radar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gpw.radar.domain.Stock;
import com.gpw.radar.domain.StockIndicators;

public interface StockIndicatorsRepository extends JpaRepository<StockIndicators, Long>{
	StockIndicators findByStock(Stock stock);
	
	@Query("from StockIndicators si join fetch si.stock")
	List<StockIndicators> findAllStocksFetchStockIndicators();
}
