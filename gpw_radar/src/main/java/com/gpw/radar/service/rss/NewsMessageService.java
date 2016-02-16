package com.gpw.radar.service.rss;

import com.gpw.radar.domain.enumeration.RssType;
import com.gpw.radar.domain.rss.NewsMessage;
import com.gpw.radar.repository.rss.NewsMessageRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class NewsMessageService {

	@Inject
	NewsMessageRepository newsMessageRepository;

	public ResponseEntity<List<NewsMessage>> getLatestNewsMessageByType(RssType type, Pageable page) {
		List<NewsMessage> latestNewsMessage = newsMessageRepository.findByType(type, page).getContent();
		return new ResponseEntity<List<NewsMessage>>(latestNewsMessage, HttpStatus.OK);
	}
}
