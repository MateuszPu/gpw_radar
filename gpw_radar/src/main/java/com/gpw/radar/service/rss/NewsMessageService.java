package com.gpw.radar.service.rss;

import com.gpw.radar.domain.enumeration.RssType;
import com.gpw.radar.domain.rss.NewsMessage;
import com.gpw.radar.repository.rss.NewsMessageRepository;
import com.gpw.radar.service.chat.RssObserver;
import com.gpw.radar.web.rest.dto.rssNews.NewsDetailsDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
public class NewsMessageService implements RssObserver, NewsMessageServiceable {

    @Inject
    private NewsMessageRepository newsMessageRepository;

    @Inject
    private RssObservable rssParserService;

    @PostConstruct
    private void init() {
        rssParserService.addRssObserver(this);
    }

    public ResponseEntity<List<NewsDetailsDTO>> getLatestNewsMessageByType(RssType type) {
        Pageable pageable = new PageRequest(0, 5, Sort.Direction.DESC, "createdDate");
        List<NewsMessage> latestNewsMessage = newsMessageRepository.findByType(type, pageable).getContent();
        return new ResponseEntity<List<NewsDetailsDTO>>(mapToDTO(latestNewsMessage), HttpStatus.OK);
    }

    public ResponseEntity<List<NewsDetailsDTO>> getMessagesByTypeBetweenDates(RssType type, ZonedDateTime startDate, ZonedDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            return new ResponseEntity<List<NewsDetailsDTO>>(HttpStatus.BAD_REQUEST);
        }
        List<NewsMessage> latestNewsMessageDateRange = newsMessageRepository.findByTypeAndCreatedDateAfterAndCreatedDateBefore(type, startDate, endDate);

        return new ResponseEntity<List<NewsDetailsDTO>>(mapToDTO(latestNewsMessageDateRange), HttpStatus.OK);
    }

    public ResponseEntity<List<NewsDetailsDTO>> getLatestTop5NewsMessage() {
        Set<NewsMessage> latestNewsMessage = newsMessageRepository.findTop5ByOrderByCreatedDateDesc();
        return new ResponseEntity<List<NewsDetailsDTO>>(mapToDTO(latestNewsMessage), HttpStatus.OK);
    }

    private List<NewsDetailsDTO> mapToDTO(Collection<NewsMessage> messages) {
        ModelMapper modelMapper = new ModelMapper();
        Type dtoType = new TypeToken<List<NewsDetailsDTO>>() {
        }.getType();
        List<NewsDetailsDTO> dto = modelMapper.map(messages, dtoType);
        return dto;
    }

    @Override
    public void updateRssNewsMessage(List<NewsMessage> parsedRssNewsMessage) {
        parsedRssNewsMessage.forEach(nws -> newsMessageRepository.save(nws));
    }

}
