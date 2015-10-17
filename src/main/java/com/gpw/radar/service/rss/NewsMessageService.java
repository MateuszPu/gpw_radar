package com.gpw.radar.service.rss;

import java.util.List;

import javax.inject.Inject;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.gpw.radar.domain.enumeration.RssType;
import com.gpw.radar.domain.rss.NewsMessage;
import com.gpw.radar.repository.rss.NewsMessageRepository;

@Service
public class NewsMessageService {

	@Inject
	NewsMessageRepository newsMessageRepository;

	public ResponseEntity<List<NewsMessage>> getLatestNewsMessageByType(RssType type) {
		Pageable page = new PageRequest(0, 5, Direction.DESC, "createdDate");
		List<NewsMessage> latestNewsMessage = newsMessageRepository.findByType(type, page).getContent();
		return new ResponseEntity<List<NewsMessage>>(latestNewsMessage, HttpStatus.OK);
	}
}
