package com.gpw.radar.elasticsearch.newsmessage.dao;

import com.gpw.radar.dao.newsmessage.NewsMessageDAO;
import com.gpw.radar.elasticsearch.newsmessage.NewsMessage;
import com.gpw.radar.elasticsearch.newsmessage.repository.NewsMessageEsRepository;
import com.gpw.radar.rabbitmq.consumer.rss.news.RssType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service("newsMessageEsDAO")
public class NewsMessageEsDAO implements NewsMessageDAO<NewsMessage> {

    private final NewsMessageEsRepository newsMessageEsRepository;

    @Autowired
    public NewsMessageEsDAO(NewsMessageEsRepository newsMessageEsRepository) {
        this.newsMessageEsRepository = newsMessageEsRepository;
    }

    @Override
    public List<NewsMessage> findTop5ByTypeOrderByNewsDateTimeDesc(RssType type) {
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, NewsMessage.DATE_TIME_FIELD_NAME));
        Pageable pageable = new PageRequest(0, 5, sort);
        return newsMessageEsRepository.findTop5ByType(type, pageable);
    }

    @Override
    public List<NewsMessage> findByTypeAndNewsDateTimeAfterAndNewsDateTimeBefore(RssType type,
                                                                                 LocalDateTime startDate,
                                                                                 LocalDateTime endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return newsMessageEsRepository.findByTypeAndNewsDateTimeAfterAndNewsDateTimeBefore(type,
            startDate.format(formatter), endDate.format(formatter));
    }

    @Override
    public NewsMessage save(NewsMessage news) {
        return newsMessageEsRepository.save(news);
    }

    @Override
    public Iterable<NewsMessage> save(Iterable<NewsMessage> newsMessages) {
        return newsMessageEsRepository.save(newsMessages);
    }

    @Override
    public List<NewsMessage> findTop5ByOrderByNewsDateTimeDesc() {
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, NewsMessage.DATE_TIME_FIELD_NAME));
        Pageable pageable = new PageRequest(0, 5, sort);
        return newsMessageEsRepository.findAll(pageable).getContent();
    }
}
