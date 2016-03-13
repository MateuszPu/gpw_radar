package com.gpw.radar.web.rest.rss;

import com.gpw.radar.domain.enumeration.RssType;
import com.gpw.radar.security.AuthoritiesConstants;
import com.gpw.radar.service.rss.NewsMessageService;
import com.gpw.radar.web.rest.dto.rssNews.NewsDetailsDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/news/message")
@RolesAllowed(AuthoritiesConstants.USER)
public class NewsMessageResource {

	@Inject
	private NewsMessageService newsMessageService;

	@RequestMapping(value = "/latest", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<NewsDetailsDTO>> getNewsMessageByType(@RequestParam RssType type, @RequestParam int page, @RequestParam  int size) {
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "createdDate");
        return newsMessageService.getLatestNewsMessageByType(type, pageable);
    }

    @RequestMapping(value = "/range", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<NewsDetailsDTO>> getNewsMessageByTypeAndDateRange(@RequestParam RssType type, @RequestParam String startDate, @RequestParam  String endDate) {
        return newsMessageService.getMessagesByTypeBetweenDates(type, ZonedDateTime.parse(startDate.replaceAll("\"", "")), ZonedDateTime.parse(endDate.replaceAll("\"", "")));
    }
}
