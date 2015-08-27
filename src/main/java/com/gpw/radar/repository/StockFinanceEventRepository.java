package com.gpw.radar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gpw.radar.domain.Stock;
import com.gpw.radar.domain.StockFinanceEvent;

/**
 * Spring Data JPA repository for the StockFinanceEvent entity.
 */
public interface StockFinanceEventRepository extends JpaRepository<StockFinanceEvent,Long> {
	List<StockFinanceEvent> findByStock(Stock stock);
	
	@Query("from StockFinanceEvent se join fetch se.stock st join fetch st.users u where u.id = :userId")
	List<StockFinanceEvent> getFollowedStockFinanceEvent(@Param("userId") long userId);

	@Query("from StockFinanceEvent se join fetch se.stock")
	List<StockFinanceEvent> getAllFetchStock();
}
