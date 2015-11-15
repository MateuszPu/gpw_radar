package com.gpw.radar.repository.stock;

import com.gpw.radar.domain.stock.StockFiveMinutesDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockFiveMinutesDetailsRepository extends JpaRepository<StockFiveMinutesDetails,Long> {

}
