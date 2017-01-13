package com.gpw.radar.repository.stock;

import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockDetailsRepository extends JpaRepository<StockDetails, String> {

    Page<StockDetails> findByStockOrderByDateDesc(Stock stock, Pageable pageable);

}
