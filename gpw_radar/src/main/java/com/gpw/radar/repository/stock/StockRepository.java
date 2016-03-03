package com.gpw.radar.repository.stock;

import com.gpw.radar.domain.User;
import com.gpw.radar.domain.enumeration.Ticker;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Spring Data JPA repository for the Stock entity.
 */
public interface StockRepository extends JpaRepository<Stock, Long> {

	Stock findByTicker(Ticker ticker);
	List<Stock> findAllByOrderByTickerAsc();
    Set<Stock> findByUsers(User user);
	Optional<Stock> findByStockName(String stockName);

	@Query(value = "SELECT s.ticker, COUNT(s.ticker)\n"
			+ "FROM stock s INNER JOIN user_stocks us ON s.id = us.stock_id \n"
			+ "INNER JOIN user u on us.user_id = u.id GROUP BY s.ticker ORDER BY count(*) DESC LIMIT 5", nativeQuery = true)
	List<StockStatistic> getTop5MostFollowedStocks();

	@Query("select count(si) from StockIndicators si where si.percentReturn > 0")
	Long countUpStocks();

	@Query("select count(si) from StockIndicators si where si.percentReturn < 0")
	Long countDownStocks();

	@Query("select count(si) from StockIndicators si where si.percentReturn = 0")
	Long countNoChangeStocks();

}
