package com.gpw.radar.web.rest.rss;

import java.util.List;

import javax.inject.Inject;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gpw.radar.domain.enumeration.RssType;
import com.gpw.radar.domain.rss.NewsMessage;
import com.gpw.radar.service.rss.NewsMessageService;

@RestController
@RequestMapping("/api/news/message")
public class NewsMessageResource {

	@Inject
	private NewsMessageService newsMessageService;

	@RequestMapping(value = "/get/latest/by/type", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<NewsMessage>> getTop10NewsMessageByType(@RequestParam RssType type) {
		return newsMessageService.getLatestNewsMessageByType(type);
	}

}
