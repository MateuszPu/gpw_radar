package com.gpw.radar.service.rss;

import com.gpw.radar.domain.enumeration.RssType;
import com.gpw.radar.domain.rss.NewsMessage;
import com.gpw.radar.repository.rss.NewsMessageRepository;
import com.gpw.radar.web.rest.dto.rssNews.NewsDetailsDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class NewsMessageService {

	@Inject
	private NewsMessageRepository newsMessageRepository;

	public ResponseEntity<List<NewsDetailsDTO>> getLatestNewsMessageByType(RssType type, Pageable page) {
		List<NewsMessage> latestNewsMessage = newsMessageRepository.findByType(type, page).getContent();

        return new ResponseEntity<List<NewsDetailsDTO>>(mapToDTO(latestNewsMessage), HttpStatus.OK);
	}

    public ResponseEntity<List<NewsDetailsDTO>> getMessagesByTypeBetweenDates(RssType type, ZonedDateTime startDate, ZonedDateTime endDate) {
        if(startDate.isAfter(endDate)) {
            return new ResponseEntity<List<NewsDetailsDTO>>(HttpStatus.BAD_REQUEST);
        }
        List<NewsMessage> latestNewsMessageDateRange = newsMessageRepository.findByTypeAndCreatedDateAfterAndCreatedDateBefore(type, startDate, endDate);

        return new ResponseEntity<List<NewsDetailsDTO>>(mapToDTO(latestNewsMessageDateRange), HttpStatus.OK);
    }

    private List<NewsDetailsDTO> mapToDTO(List<NewsMessage> messages) {
        ModelMapper modelMapper = new ModelMapper();
        Type dtoType = new TypeToken<List<NewsDetailsDTO>>() {}.getType();
        List<NewsDetailsDTO> dto = modelMapper.map(messages, dtoType);
        return dto;
    }
}
