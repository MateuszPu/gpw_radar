package com.gpw.radar.repository.stock;

import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockIndicators;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StockIndicatorsRepository extends JpaRepository<StockIndicators, Long>{
	StockIndicators findByStock(Stock stock);

	@Query("from StockIndicators si inner join si.stock")
	List<StockIndicators> findAllStocksFetchStockIndicators();

	@Query(value = "from StockIndicators si join fetch si.stock where si.slopeSimpleRegression10Days > 1 order by si.slopeSimpleRegression10Days desc", countQuery = "select count(si.stock) from StockIndicators si where si.slopeSimpleRegression10Days > 1")
	Page<StockIndicators> findWithStocksIndicators10DaysTrendUp(Pageable pageable);

	@Query(value = "from StockIndicators si join fetch si.stock where si.slopeSimpleRegression30Days > 1 order by si.slopeSimpleRegression30Days desc", countQuery = "select count(si.stock) from StockIndicators si where si.slopeSimpleRegression30Days > 1")
	Page<StockIndicators> findWithStocksIndicators30DaysTrendUp(Pageable pageable);

	@Query(value = "from StockIndicators si join fetch si.stock where si.slopeSimpleRegression60Days > 1 order by si.slopeSimpleRegression60Days desc", countQuery = "select count(si.stock) from StockIndicators si where si.slopeSimpleRegression60Days > 1")
	Page<StockIndicators> findWithStocksIndicators60DaysTrendUp(Pageable pageable);

	@Query(value = "from StockIndicators si join fetch si.stock where si.slopeSimpleRegression90Days > 1 order by si.slopeSimpleRegression90Days desc", countQuery = "select count(si.stock) from StockIndicators si where si.slopeSimpleRegression90Days > 1")
	Page<StockIndicators> findWithStocksIndicators90DaysTrendUp(Pageable pageable);

	@Query(value = "from StockIndicators si join fetch si.stock where si.slopeSimpleRegression10Days < -1 order by si.slopeSimpleRegression10Days asc", countQuery = "select count(si.stock) from StockIndicators si where si.slopeSimpleRegression10Days < -1")
	Page<StockIndicators> findWithStocksIndicators10DaysTrendDown(Pageable pageable);

	@Query(value = "from StockIndicators si join fetch si.stock where si.slopeSimpleRegression30Days < -1 order by si.slopeSimpleRegression30Days asc", countQuery = "select count(si.stock) from StockIndicators si where si.slopeSimpleRegression30Days < -1")
	Page<StockIndicators> findWithStocksIndicators30DaysTrendDown(Pageable pageable);

	@Query(value = "from StockIndicators si join fetch si.stock where si.slopeSimpleRegression60Days < -1 order by si.slopeSimpleRegression60Days asc", countQuery = "select count(si.stock) from StockIndicators si where si.slopeSimpleRegression60Days < -1")
	Page<StockIndicators> findWithStocksIndicators60DaysTrendDown(Pageable pageable);

	@Query(value = "from StockIndicators si join fetch si.stock where si.slopeSimpleRegression90Days < -1 order by si.slopeSimpleRegression90Days asc", countQuery = "select count(si.stock) from StockIndicators si where si.slopeSimpleRegression90Days < -1")
	Page<StockIndicators> findWithStocksIndicators90DaysTrendDown(Pageable pageable);
}
