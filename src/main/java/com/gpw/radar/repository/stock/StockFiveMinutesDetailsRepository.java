package com.gpw.radar.repository.stock;

import com.gpw.radar.domain.stock.StockFiveMinutesDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface StockFiveMinutesDetailsRepository extends JpaRepository<StockFiveMinutesDetails,Long> {

    List<StockFiveMinutesDetails> findByDate(LocalDate date);

}
