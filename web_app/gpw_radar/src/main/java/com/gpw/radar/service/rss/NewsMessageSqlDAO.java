package com.gpw.radar.service.rss;

import com.gpw.radar.dao.newsmessage.NewsMessageDAO;
import com.gpw.radar.domain.rss.NewsMessage;
import com.gpw.radar.rabbitmq.consumer.rss.news.RssType;
import com.gpw.radar.repository.rss.NewsMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service("newsMessageSqlDAO")
public class NewsMessageSqlDAO implements NewsMessageDAO {

    private final NewsMessageRepository newsMessageRepository;

    @Autowired
    public NewsMessageSqlDAO(NewsMessageRepository newsMessageRepository) {
        this.newsMessageRepository = newsMessageRepository;
    }

    @Override
    public List<NewsMessage> findTop5ByTypeOrderByNewsDateTimeDesc(RssType type) {
        return newsMessageRepository.findTop5ByTypeOrderByNewsDateTimeDesc(type);
    }

    @Override
    public List<NewsMessage> findByTypeAndNewsDateTimeAfterAndNewsDateTimeBefore(RssType type, LocalDateTime startDate, LocalDateTime endDate) {
        return newsMessageRepository.findByTypeAndNewsDateTimeAfterAndNewsDateTimeBefore(type, startDate, endDate);
    }

    @Override
    public NewsMessage save(NewsMessage news) {
        return newsMessageRepository.save(news);
    }

    @Override
    public Iterable<NewsMessage> save(Iterable<NewsMessage> newsMessages) {
        return newsMessageRepository.save(newsMessages);
    }


    @Override
    public Set<NewsMessage> findTop5ByOrderByNewsDateTimeDesc() {
        return newsMessageRepository.findTop5ByOrderByNewsDateTimeDesc();
    }
}
