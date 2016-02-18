package com.gpw.radar.web.rest.rss;

import com.gpw.radar.domain.enumeration.RssType;
import com.gpw.radar.domain.rss.NewsMessage;
import com.gpw.radar.service.rss.NewsMessageService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/news/message")
public class NewsMessageResource {

	@Inject
	private NewsMessageService newsMessageService;

	@RequestMapping(value = "/latest", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<NewsMessage>> getNewsMessageByType(@RequestParam RssType type, @RequestParam int page, @RequestParam  int size) {
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "createdDate");
        return newsMessageService.getLatestNewsMessageByType(type, pageable);
    }

    @RequestMapping(value = "/range", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<NewsMessage>> getNewsMessageByTypeAndDateRange(@RequestParam RssType type, @RequestParam String startDate, @RequestParam  String endDate) {
        return newsMessageService.getMessagesByTypeBetweenDates(type, ZonedDateTime.parse(startDate.replaceAll("\"", "")), ZonedDateTime.parse(endDate.replaceAll("\"", "")));
    }
}
