package com.gpw.radar.repository.rss;

import com.gpw.radar.domain.enumeration.RssType;
import com.gpw.radar.domain.rss.NewsMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsMessageRepository extends JpaRepository<NewsMessage, Long> {

	Page<NewsMessage> findByType(RssType type, Pageable pageable);
}