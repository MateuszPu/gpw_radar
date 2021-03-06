package com.gpw.radar.service.rss;

import com.gpw.radar.dao.newsmessage.NewsMessageDAO;
import com.gpw.radar.elasticsearch.newsmessage.NewsMessage;
import com.gpw.radar.rabbitmq.consumer.rss.news.RssType;
import com.gpw.radar.service.parser.DateAndTimeParserService;
import com.gpw.radar.web.rest.dto.rssNews.NewsDetailsDTO;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class NewsMessageServiceTest {

    private final NewsMessageDAO<NewsMessage> newsMessageRepository = Mockito.mock(NewsMessageDAO.class);
    private final DateAndTimeParserService dateAndTimeParserService = Mockito.mock(DateAndTimeParserService.class);

    private NewsMessageServiceable objectUnderTest = new NewsMessageService(newsMessageRepository,
        dateAndTimeParserService);

    @Test
    public void shouldReturnBadResponseForNotParsableDate() {
        ResponseEntity<List<NewsDetailsDTO>> bothNotParsable = objectUnderTest
            .getMessagesByTypeBetweenDates(RssType.CHALLENGE, "notParsable", "notParsable");
        ResponseEntity<List<NewsDetailsDTO>> startDateNotParsable = objectUnderTest
            .getMessagesByTypeBetweenDates(RssType.PAP, "notParsable", "2016-12-24");
        ResponseEntity<List<NewsDetailsDTO>> endDateNotParsable = objectUnderTest
            .getMessagesByTypeBetweenDates(RssType.RECOMMENDATIONS, "2016-12-24", "notParsable");

        assertThat(bothNotParsable.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(startDateNotParsable.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(endDateNotParsable.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldReturnBadResponseForStartDateAfterEndDate() {
        //given
        String startDate = "2016-12-01";
        String endDate = "2016-11-01";
        given(dateAndTimeParserService.parseLocalDateFromString(startDate)).willReturn(LocalDate.of(2016, 12, 1));
        given(dateAndTimeParserService.parseLocalDateFromString(endDate)).willReturn(LocalDate.of(2016, 11, 1));

        //when
        ResponseEntity<List<NewsDetailsDTO>> startAfterEnd = objectUnderTest
            .getMessagesByTypeBetweenDates(RssType.CHALLENGE, startDate, endDate);

        //then
        assertThat(startAfterEnd.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldCallRepositoryAndMapperWhenProvideCorrectData() {
        //given
        String startDate = "2016-10-01";
        String endDate = "2016-11-01";
        given(dateAndTimeParserService.parseLocalDateFromString(startDate)).willReturn(LocalDate.of(2016, 10, 1));
        given(dateAndTimeParserService.parseLocalDateFromString(endDate)).willReturn(LocalDate.of(2016, 11, 1));

        //when
        objectUnderTest.getMessagesByTypeBetweenDates(RssType.CHALLENGE, startDate, endDate);

        //then
        verify(newsMessageRepository, times(1))
            .findByTypeAndNewsDateTimeAfterAndNewsDateTimeBefore(eq(RssType.CHALLENGE), any(), any());
    }

    @Test
    public void shouldCallRepositoryAndMapper_1() {
        objectUnderTest.getLatestNewsMessageByType(any());

        verify(newsMessageRepository, times(1))
            .findTop5ByTypeOrderByNewsDateTimeDesc(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldCallRepositoryAndMapper_2() {
        objectUnderTest.getLatestTop5NewsMessage();

        verify(newsMessageRepository, times(1))
            .findTop5ByOrderByNewsDateTimeDesc();
    }
}
