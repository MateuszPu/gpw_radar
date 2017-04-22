package com.gpw.radar.elasticsearch.newsmessage.repository;

import com.gpw.radar.elasticsearch.newsmessage.NewsMessage;
import com.gpw.radar.rabbitmq.consumer.rss.news.RssType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface NewsMessageEsRepository extends ElasticsearchRepository<NewsMessage, String> {

    List<NewsMessage> findTop5ByType(RssType type, Pageable pageable);

    List<NewsMessage> findByTypeAndNewsDateTimeAfterAndNewsDateTimeBefore(RssType type,
                                                                          String startDate,
                                                                          String endDate);

    NewsMessage save(NewsMessage news);

}
