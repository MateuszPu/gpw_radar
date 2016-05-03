package com.gpw.radar.service.rss;

import com.gpw.radar.domain.enumeration.RssType;
import com.gpw.radar.domain.rss.NewsMessage;
import com.gpw.radar.web.rest.dto.rssNews.NewsDetailsDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.ZonedDateTime;
import java.util.List;

public interface NewsMessageServiceable {

    ResponseEntity<List<NewsDetailsDTO>> getLatestNewsMessageByType(RssType type);
    ResponseEntity<List<NewsDetailsDTO>> getMessagesByTypeBetweenDates(RssType type, ZonedDateTime startDate, ZonedDateTime endDate);
}

