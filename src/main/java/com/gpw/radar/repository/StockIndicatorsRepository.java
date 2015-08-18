package com.gpw.radar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gpw.radar.domain.Stock;
import com.gpw.radar.domain.StockIndicators;

public interface StockIndicatorsRepository extends JpaRepository<StockIndicators, Long>{
	StockIndicators findByStock(Stock stock);
}
