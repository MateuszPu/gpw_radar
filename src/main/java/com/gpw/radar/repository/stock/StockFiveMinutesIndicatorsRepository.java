package com.gpw.radar.repository.stock;

import com.gpw.radar.domain.stock.StockFiveMinutesIndicators;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockFiveMinutesIndicatorsRepository extends JpaRepository<StockFiveMinutesIndicators, Long> {
}
