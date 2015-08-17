package com.gpw.radar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gpw.radar.domain.Stock;
import com.gpw.radar.domain.StockFinanceEvent;

/**
 * Spring Data JPA repository for the StockFinanceEvent entity.
 */
public interface StockFinanceEventRepository extends JpaRepository<StockFinanceEvent,Long> {
	List<StockFinanceEvent> findByStock(Stock stock);
}