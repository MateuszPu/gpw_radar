package com.gpw.radar.repository.stock;

import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockFiveMinutesIndicators;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockFiveMinutesIndicatorsRepository extends JpaRepository<StockFiveMinutesIndicators, Long> {

    List<StockFiveMinutesIndicators> findByStock(Stock stock);
}
