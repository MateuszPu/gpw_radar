package com.gpw.radar.repository;

import com.gpw.radar.domain.StockDetails;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the StockDetails entity.
 */
public interface StockDetailsRepository extends JpaRepository<StockDetails,Long> {

}
