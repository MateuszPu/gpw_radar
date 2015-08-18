package com.gpw.radar.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gpw.radar.domain.Stock;
import com.gpw.radar.domain.StockStatistic;
import com.gpw.radar.domain.enumeration.Tickers;

/**
 * Spring Data JPA repository for the Stock entity.
 */
public interface StockRepository extends JpaRepository<Stock, Long> {

	Stock findByTicker(Tickers ticker);
	List<Stock> findAllByOrderByTickerAsc();

	@Query(value = "SELECT s.ticker, COUNT(s.ticker)\n"
			+ "FROM t_stock s INNER JOIN t_user_stocks us ON s.id = us.stock_id \n"
			+ "INNER JOIN t_user u on us.user_id = u.id GROUP BY s.ticker ORDER BY count(*) DESC LIMIT 5", nativeQuery = true)
	List<StockStatistic> getTop5MostFollowedStocks();

	@Query("select count(si) from StockIndicators si where si.percentReturn > 0")
	Long countUpStocks();

	@Query("select count(si) from StockIndicators si where si.percentReturn < 0")
	Long countDownStocks();

	@Query("select count(si) from StockIndicators si where si.percentReturn = 0")
	Long countNoChangeStocks();
	
	@Query(value = "select si.stock from StockIndicators si where si.slopeSimpleRegression10Days > 1 order by si.slopeSimpleRegression10Days desc", countQuery = "select count(si.stock) from StockIndicators si where si.slopeSimpleRegression10Days > 1")
	Page<Stock> findWithStocksIndicators10DaysTrendUp(Pageable pageable);
	
	@Query(value = "select si.stock from StockIndicators si where si.slopeSimpleRegression30Days > 1 order by si.slopeSimpleRegression30Days desc", countQuery = "select count(si.stock) from StockIndicators si where si.slopeSimpleRegression30Days > 1")
	Page<Stock> findWithStocksIndicators30DaysTrendUp(Pageable pageable);
	
	@Query(value = "select si.stock from StockIndicators si where si.slopeSimpleRegression60Days > 1 order by si.slopeSimpleRegression60Days desc", countQuery = "select count(si.stock) from StockIndicators si where si.slopeSimpleRegression60Days > 1")
	Page<Stock> findWithStocksIndicators60DaysTrendUp(Pageable pageable);
	
	@Query(value = "select si.stock from StockIndicators si where si.slopeSimpleRegression90Days > 1 order by si.slopeSimpleRegression90Days desc", countQuery = "select count(si.stock) from StockIndicators si where si.slopeSimpleRegression90Days > 1")
	Page<Stock> findWithStocksIndicators90DaysTrendUp(Pageable pageable);
	
	@Query(value = "select si.stock from StockIndicators si where si.slopeSimpleRegression10Days < -1 order by si.slopeSimpleRegression10Days desc", countQuery = "select count(si.stock) from StockIndicators si where si.slopeSimpleRegression10Days < -1")
	Page<Stock> findWithStocksIndicators10DaysTrendDown(Pageable pageable);
	
	@Query(value = "select si.stock from StockIndicators si where si.slopeSimpleRegression30Days < -1 order by si.slopeSimpleRegression30Days desc", countQuery = "select count(si.stock) from StockIndicators si where si.slopeSimpleRegression30Days < -1")
	Page<Stock> findWithStocksIndicators30DaysTrendDown(Pageable pageable);
	
	@Query(value = "select si.stock from StockIndicators si where si.slopeSimpleRegression60Days < -1 order by si.slopeSimpleRegression60Days desc", countQuery = "select count(si.stock) from StockIndicators si where si.slopeSimpleRegression60Days < -1")
	Page<Stock> findWithStocksIndicators60DaysTrendDown(Pageable pageable);
	
	@Query(value = "select si.stock from StockIndicators si where si.slopeSimpleRegression90Days < -1 order by si.slopeSimpleRegression90Days desc", countQuery = "select count(si.stock) from StockIndicators si where si.slopeSimpleRegression90Days < -1")
	Page<Stock> findWithStocksIndicators90DaysTrendDown(Pageable pageable);
}