package com.gpw.radar.service.rss;

import com.gpw.radar.domain.rss.NewsMessage;
import com.gpw.radar.rabbitmq.consumer.rss.news.RssType;
import com.gpw.radar.repository.rss.NewsMessageRepository;
import com.gpw.radar.web.rest.dto.rssNews.NewsDetailsDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
public class NewsMessageService implements NewsMessageServiceable {

    private final NewsMessageRepository newsMessageRepository;

    @Autowired
    public NewsMessageService(NewsMessageRepository newsMessageRepository) {
        this.newsMessageRepository = newsMessageRepository;
    }

    public ResponseEntity<List<NewsDetailsDTO>> getLatestNewsMessageByType(RssType type) {
        List<NewsMessage> latestNewsMessage = newsMessageRepository.findTop5ByTypeOrderByNewsDateTimeDesc(type);
        return new ResponseEntity<>(mapToDTO(latestNewsMessage), HttpStatus.OK);
    }

    public ResponseEntity<List<NewsDetailsDTO>> getMessagesByTypeBetweenDates(RssType type, String startDateString, String endDateString) {
        LocalDateTime startDate = getLocalDateTime(startDateString);
        LocalDateTime endDate = getLocalDateTime(endDateString);

        if (startDate.isAfter(endDate)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<NewsMessage> latestNewsMessageDateRange = newsMessageRepository.findByTypeAndNewsDateTimeAfterAndNewsDateTimeBefore(type, startDate, endDate);
        return new ResponseEntity<>(mapToDTO(latestNewsMessageDateRange), HttpStatus.OK);
    }

    private LocalDateTime getLocalDateTime(String stringDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        return LocalDateTime.parse(stringDate, formatter);
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
