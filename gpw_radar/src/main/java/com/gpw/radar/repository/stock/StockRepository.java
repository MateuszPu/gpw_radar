package com.gpw.radar.repository.stock;

import com.gpw.radar.config.CacheConfiguration;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockStatistic;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Spring Data JPA repository for the Stock entity.
 */
public interface StockRepository extends JpaRepository<Stock, String> {

    @Cacheable(value = CacheConfiguration.STOCK_CACHE)
    Stock findByTicker(String ticker);

    @Cacheable(value = CacheConfiguration.STOCK_TICKERS_CACHE)
    @Query(value = "SELECT ticker from Stock", nativeQuery = true)
    Set<String> findAllTickers();

    @Cacheable(value = CacheConfiguration.STOCKS_FOLLOWED_BY_USER_CACHE, key = "#p0")
    @Query(value = "SELECT * from stock where id in (select stock_id from user_stocks where user_id = :userId)", nativeQuery = true)
    List<Stock> findStocksByUserId(@Param("userId") Long userId);

    @Query("from Stock st left outer join fetch st.stockIndicators")
    List<Stock> findAllFetchIndicators();

    Optional<Stock> findByStockName(String stockName);

    @Query(value = "SELECT COUNT(us.stock_id), st.ticker" +
        "  FROM user_stocks us inner join stock st on us.stock_id = st.id" +
        " GROUP BY st.ticker order by COUNT(us.stock_id) desc limit 5", nativeQuery = true)
    List<Object[]> getTop5MostFollowedStocks();

    @Query("select count(si) from StockIndicators si where si.percentReturn > 0")
    Long countUpStocks();

    @Query("select count(si) from StockIndicators si where si.percentReturn < 0")
    Long countDownStocks();

    @Query("select count(si) from StockIndicators si where si.percentReturn = 0")
    Long countNoChangeStocks();
}
