package com.gpw.radar.repository.stock;

import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockFinanceEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StockFinanceEventRepository extends JpaRepository<StockFinanceEvent,Long> {
	List<StockFinanceEvent> findByStock(Stock stock);

	@Query("from StockFinanceEvent se join fetch se.stock st join fetch st.users u where u.id = :userId")
	List<StockFinanceEvent> getFollowedStockFinanceEvent(@Param("userId") long userId);

	@Query("from StockFinanceEvent se join fetch se.stock")
	List<StockFinanceEvent> getAllFetchStock();
}
