package com.gpw.radar.elasticsearch.newsmessage.repository;

import com.gpw.radar.elasticsearch.newsmessage.NewsMessage;
import com.gpw.radar.elasticsearch.stockdetails.StockDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface NewsMessageEsRepository extends ElasticsearchRepository<NewsMessage, String> {

}
