package com.gpw.radar.repository.stock;

import com.gpw.radar.domain.stock.StockFiveMinutesDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface StockFiveMinutesDetailsRepository extends JpaRepository<StockFiveMinutesDetails, String> {

    @Query("from StockFiveMinutesDetails details join fetch details.stock where details.date= :date and details.time= :time")
    Optional<List<StockFiveMinutesDetails>> findByDateAndTime(@Param("date") LocalDate date, @Param("time") LocalTime time);
}
