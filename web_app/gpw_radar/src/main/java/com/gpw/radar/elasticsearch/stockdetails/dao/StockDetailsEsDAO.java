package com.gpw.radar.elasticsearch.stockdetails.dao;

import com.gpw.radar.elasticsearch.stockdetails.StockDetails;
import com.gpw.radar.elasticsearch.stockdetails.repository.StockDetailsEsRepository;
import com.gpw.radar.dao.stockdetails.StockDetailsDAO;
import com.gpw.radar.web.rest.errors.exceptions.ResourceNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service("stockDetailsElasticSearchDAO")
public class StockDetailsEsDAO implements StockDetailsDAO {

    private final StockDetailsEsRepository stockDetailsEsRepository;

    @Autowired
    public StockDetailsEsDAO(StockDetailsEsRepository stockDetailsEsRepository) {
        this.stockDetailsEsRepository = stockDetailsEsRepository;
    }

    @Override
    public LocalDate findTopDate() {
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "date"));
        Optional<StockDetails> topByOrderByDateDesc = stockDetailsEsRepository.findAll(new PageRequest(0, 1, sort))
            .getContent()
            .stream()
            .findFirst();
        return topByOrderByDateDesc.orElseThrow(ResourceNotExistException::new).getDate();
    }

    @Override
    public Iterable<StockDetails> save(Iterable<StockDetails> stockDetails) {
        return stockDetailsEsRepository.save(stockDetails);
    }

    @Override
    public StockDetails save(StockDetails stockDetails) {
        return stockDetailsEsRepository.save(stockDetails);
    }

    @Override
    public List<StockDetails> findByStockTickerOrderByDateDesc(String ticker, Pageable pageable) {
        Page<StockDetails> byStockTickerOrderByDateDesc = stockDetailsEsRepository.findByStockTickerOrderByDateDesc(ticker, pageable);
        return byStockTickerOrderByDateDesc.getContent();
    }

}
