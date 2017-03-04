package com.gpw.radar.service.rss;

import com.gpw.radar.dao.newsmessage.NewsMessageDAO;
import com.gpw.radar.domain.rss.NewsMessage;
import com.gpw.radar.rabbitmq.consumer.rss.news.RssType;
import com.gpw.radar.service.mapper.DtoMapper;
import com.gpw.radar.service.parser.DateAndTimeParserService;
import com.gpw.radar.web.rest.dto.rssNews.NewsDetailsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.gpw.radar.config.CustomDateTimeFormat.*;

@Service
public class NewsMessageService implements NewsMessageServiceable {

    private final NewsMessageDAO newsMessageRepository;
    private final DateAndTimeParserService dateAndTimeParserService;
    private final DtoMapper<NewsMessage, NewsDetailsDTO> dtoMapper = new DtoMapper<>(NewsDetailsDTO.class);

    @Autowired
    public NewsMessageService(@Qualifier("newsMessageSqlDAO") NewsMessageDAO newsMessageRepository,
                              DateAndTimeParserService dateAndTimeParserService) {
        this.newsMessageRepository = newsMessageRepository;
        this.dateAndTimeParserService = dateAndTimeParserService;
    }

    public ResponseEntity<List<NewsDetailsDTO>> getLatestNewsMessageByType(RssType type) {
        List<NewsMessage> latestNewsMessage = newsMessageRepository.findTop5ByTypeOrderByNewsDateTimeDesc(type);
        List<NewsDetailsDTO> newsDetailsDtos = dtoMapper.mapToDto(latestNewsMessage);
        return new ResponseEntity<>(newsDetailsDtos, HttpStatus.OK);
    }

    public ResponseEntity<List<NewsDetailsDTO>> getMessagesByTypeBetweenDates(RssType type, String startDateString, String endDateString) {
        String localDateRegex = yearRegex + "-" + monthRegex + "-" + dayRegex;
        if (!(startDateString.matches(localDateRegex) && endDateString.matches(localDateRegex))) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        LocalDateTime startDate = dateAndTimeParserService.parseLocalDateFromString(startDateString).atStartOfDay();
        LocalDateTime endDate = dateAndTimeParserService.parseLocalDateFromString(endDateString).atTime(23, 59);

        if (startDate.isAfter(endDate)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<NewsMessage> latestNewsMessageDateRange = newsMessageRepository.findByTypeAndNewsDateTimeAfterAndNewsDateTimeBefore(type, startDate, endDate);
        List<NewsDetailsDTO> newsDetailsDtos = dtoMapper.mapToDto(latestNewsMessageDateRange);
        return new ResponseEntity<>(newsDetailsDtos, HttpStatus.OK);
    }

    public ResponseEntity<List<NewsDetailsDTO>> getLatestTop5NewsMessage() {
        Set<NewsMessage> latestNewsMessage = newsMessageRepository.findTop5ByOrderByNewsDateTimeDesc();
        List<NewsDetailsDTO> newsDetailsDtos = dtoMapper.mapToDto(latestNewsMessage);
        return new ResponseEntity<>(newsDetailsDtos, HttpStatus.OK);
    }
}
