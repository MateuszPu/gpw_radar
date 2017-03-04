package com.gpw.radar.dao.stockdetails;

import com.gpw.radar.elasticsearch.stockdetails.StockDetails;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface StockDetailsDAO {

    LocalDate findTopDate();

    Iterable<StockDetails> save(Iterable<StockDetails> stockDetails);

    StockDetails save(StockDetails stockDetails);

    List<StockDetails> findByStockTickerOrderByDateDesc(String ticker, Pageable pageable);
}
