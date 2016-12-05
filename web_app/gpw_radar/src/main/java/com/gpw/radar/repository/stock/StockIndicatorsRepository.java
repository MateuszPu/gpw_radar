package com.gpw.radar.repository.stock;

import com.gpw.radar.domain.stock.StockIndicators;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface StockIndicatorsRepository extends JpaRepository<StockIndicators, String> {
    @Query(value = "select * from stock_indicators where id = (select stock_indicators_id from stock where ticker = :ticker)", nativeQuery = true)
    Optional<StockIndicators> findByStockTicker(@Param("ticker") String ticker);

    @Query(value = "from Stock st join fetch st.stockIndicators si where si.slopeSimpleRegression10Days > 1 and si.date = :date order by si.slopeSimpleRegression10Days desc",
        countQuery = "select count(si) from StockIndicators si where si.slopeSimpleRegression10Days > 1 and si.date = :date")
    Page<StockIndicators> findWithStocksIndicators10DaysTrendUp(Pageable pageable, @Param("date") LocalDate date);

    @Query(value = "from Stock st join fetch st.stockIndicators si where si.slopeSimpleRegression30Days > 1 and si.date = :date order by si.slopeSimpleRegression30Days desc",
        countQuery = "select count(si) from StockIndicators si where si.slopeSimpleRegression30Days > 1 and si.date = :date")
    Page<StockIndicators> findWithStocksIndicators30DaysTrendUp(Pageable pageable, @Param("date") LocalDate date);

    @Query(value = "from Stock st join fetch st.stockIndicators si where si.slopeSimpleRegression60Days > 1 and si.date = :date order by si.slopeSimpleRegression60Days desc",
        countQuery = "select count(si) from StockIndicators si where si.slopeSimpleRegression60Days > 1 and si.date = :date")
    Page<StockIndicators> findWithStocksIndicators60DaysTrendUp(Pageable pageable, @Param("date") LocalDate date);

    @Query(value = "from Stock st join fetch st.stockIndicators si where si.slopeSimpleRegression90Days > 1 and si.date = :date order by si.slopeSimpleRegression90Days desc",
        countQuery = "select count(si) from StockIndicators si where si.slopeSimpleRegression90Days > 1 and si.date = :date")
    Page<StockIndicators> findWithStocksIndicators90DaysTrendUp(Pageable pageable, @Param("date") LocalDate date);

    @Query(value = "from Stock st join fetch st.stockIndicators si where si.slopeSimpleRegression10Days < -1 and si.date = :date order by si.slopeSimpleRegression10Days asc",
        countQuery = "select count(si) from StockIndicators si where si.slopeSimpleRegression10Days < -1 and si.date = :date")
    Page<StockIndicators> findWithStocksIndicators10DaysTrendDown(Pageable pageable, @Param("date") LocalDate date);

    @Query(value = "from Stock st join fetch st.stockIndicators si where si.slopeSimpleRegression30Days < -1 and si.date = :date order by si.slopeSimpleRegression30Days asc",
        countQuery = "select count(si) from StockIndicators si where si.slopeSimpleRegression30Days < -1 and si.date = :date")
    Page<StockIndicators> findWithStocksIndicators30DaysTrendDown(Pageable pageable, @Param("date") LocalDate date);

    @Query(value = "from Stock st join fetch st.stockIndicators si where si.slopeSimpleRegression60Days < -1 and si.date = :date order by si.slopeSimpleRegression60Days asc",
        countQuery = "select count(si) from StockIndicators si where si.slopeSimpleRegression60Days < -1 and si.date = :date")
    Page<StockIndicators> findWithStocksIndicators60DaysTrendDown(Pageable pageable, @Param("date") LocalDate date);

    @Query(value = "from Stock st join fetch st.stockIndicators si where si.slopeSimpleRegression90Days < -1 and si.date = :date order by si.slopeSimpleRegression90Days asc",
        countQuery = "select count(si) from StockIndicators si where si.slopeSimpleRegression90Days < -1 and si.date = :date")
    Page<StockIndicators> findWithStocksIndicators90DaysTrendDown(Pageable pageable, @Param("date") LocalDate date);
}
