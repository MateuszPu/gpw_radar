package com.gpw.radar.repository;

import com.gpw.radar.domain.StockFinanceEvent;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the StockFinanceEvent entity.
 */
public interface StockFinanceEventRepository extends JpaRepository<StockFinanceEvent,Long> {

}
