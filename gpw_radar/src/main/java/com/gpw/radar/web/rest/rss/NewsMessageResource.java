package com.gpw.radar.web.rest.rss;

import com.gpw.radar.rabbitmq.consumer.rss.news.RssType;
import com.gpw.radar.security.AuthoritiesConstants;
import com.gpw.radar.service.rss.NewsMessageServiceable;
import com.gpw.radar.web.rest.dto.rssNews.NewsDetailsDTO;
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
public class NewsMessageResource {

    @Inject
    private NewsMessageServiceable newsMessageServiceable;

    @RequestMapping(value = "/latest", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<List<NewsDetailsDTO>> getNewsMessageByType(@RequestParam RssType type) {
        return newsMessageServiceable.getLatestNewsMessageByType(type);
    }

    @RequestMapping(value = "/range", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<List<NewsDetailsDTO>> getNewsMessageByTypeAndDateRange(@RequestParam RssType type, @RequestParam String startDate, @RequestParam String endDate) {
        return newsMessageServiceable.getMessagesByTypeBetweenDates(type, ZonedDateTime.parse(startDate.replaceAll("\"", "")), ZonedDateTime.parse(endDate.replaceAll("\"", "")));
    }

    @RequestMapping(value = "/top/five/latest", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<NewsDetailsDTO>> getTopFiveLatestNewsMessage() {
        return newsMessageServiceable.getLatestTop5NewsMessage();
    }

}
