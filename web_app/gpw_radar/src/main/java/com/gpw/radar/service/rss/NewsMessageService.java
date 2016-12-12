package com.gpw.radar.service.rss;

import com.gpw.radar.domain.rss.NewsMessage;
import com.gpw.radar.rabbitmq.consumer.rss.news.RssType;
import com.gpw.radar.repository.rss.NewsMessageRepository;
import com.gpw.radar.service.parser.DateAndTimeParserService;
import com.gpw.radar.web.rest.dto.rssNews.NewsDetailsDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.gpw.radar.config.CustomDateTimeFormat.*;

@Service
public class NewsMessageService implements NewsMessageServiceable {

    private final NewsMessageRepository newsMessageRepository;
    private final DateAndTimeParserService dateAndTimeParserService;
    private final String datePattern = "yyyy-MM-dd";

    @Autowired
    public NewsMessageService(NewsMessageRepository newsMessageRepository,
                              DateAndTimeParserService dateAndTimeParserService) {
        this.newsMessageRepository = newsMessageRepository;
        this.dateAndTimeParserService = dateAndTimeParserService;
    }

    public ResponseEntity<List<NewsDetailsDTO>> getLatestNewsMessageByType(RssType type) {
        List<NewsMessage> latestNewsMessage = newsMessageRepository.findTop5ByTypeOrderByNewsDateTimeDesc(type);
        return new ResponseEntity<>(mapToDTO(latestNewsMessage), HttpStatus.OK);
    }

    public ResponseEntity<List<NewsDetailsDTO>> getMessagesByTypeBetweenDates(RssType type, String startDateString, String endDateString) {
        String localDateRegex = yearRegex + "-" + monthRegex + "-" + dayRegex;
        if (!(startDateString.matches(localDateRegex) || endDateString.matches(localDateRegex))) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        LocalDateTime startDate = dateAndTimeParserService.parseLocalDateFromString(startDateString).atStartOfDay();
        LocalDateTime endDate = dateAndTimeParserService.parseLocalDateFromString(endDateString).atTime(23, 59);

        if (startDate.isAfter(endDate)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<NewsMessage> latestNewsMessageDateRange = newsMessageRepository.findByTypeAndNewsDateTimeAfterAndNewsDateTimeBefore(type, startDate, endDate);
        return new ResponseEntity<>(mapToDTO(latestNewsMessageDateRange), HttpStatus.OK);
    }

    public ResponseEntity<List<NewsDetailsDTO>> getLatestTop5NewsMessage() {
        Set<NewsMessage> latestNewsMessage = newsMessageRepository.findTop5ByOrderByNewsDateTimeDesc();
        return new ResponseEntity<>(mapToDTO(latestNewsMessage), HttpStatus.OK);
    }

    private List<NewsDetailsDTO> mapToDTO(Collection<NewsMessage> messages) {
        ModelMapper modelMapper = new ModelMapper();
        Type dtoType = new TypeToken<List<NewsDetailsDTO>>() {
        }.getType();
        List<NewsDetailsDTO> dto = modelMapper.map(messages, dtoType);
        return dto;
    }
}
