package com.gpw.radar.elasticsearch.repository;

import com.gpw.radar.elasticsearch.domain.stockdetails.StockDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface StockDetailsEsRepository extends ElasticsearchRepository<StockDetails, String> {

    List<StockDetails> findByStockTicker(String ticker);
    Page<StockDetails> findByStockTickerOrderByDateDesc(String ticker, Pageable pageable);
}
