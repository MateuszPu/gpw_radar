package com.gpw.radar.service;

import com.gpw.radar.Application;
import com.gpw.radar.service.parser.DateAndTimeParserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.StrictAssertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class DateAndTimeParserServiceTest {

    @Inject
    private DateAndTimeParserService dateAndTimeParserService;

    @Test
    public void getLocalDateFromStringFormatOne() {
        String date = "2015-11-19";
        LocalDate localDate = dateAndTimeParserService.parseLocalDateFromString(date);
        assertThat(localDate).isEqualTo(LocalDate.of(2015, 11, 19));
    }

    @Test
    public void getLocalDateFromStringFormatTwo() {
        String date = "20140511";
        LocalDate localDate = dateAndTimeParserService.parseLocalDateFromString(date);
        assertThat(localDate).isEqualTo(LocalDate.of(2014, 5, 11));
    }

    @Test
    public void getLocalTimeFromStringFormatOno() {
        String time = "13:05:00";
        LocalTime localTime = dateAndTimeParserService.parseLocalTimeFromString(time);
        assertThat(localTime).isEqualTo(LocalTime.of(13, 5, 0));
    }

    @Test
    public void getLocalTimeFromStringFormatTwo() {
        String time = "235500";
        LocalTime localTime = dateAndTimeParserService.parseLocalTimeFromString(time);
        assertThat(localTime).isEqualTo(LocalTime.of(23, 55, 0));
    }

    @Test
    public void getFiveMinutesTime() {
        LocalTime localTime = dateAndTimeParserService.getFiveMinutesTime(LocalTime.of(15, 44, 56));
        assertThat(localTime).isEqualTo(LocalTime.of(15, 40, 0));

        localTime = dateAndTimeParserService.getFiveMinutesTime(LocalTime.of(15, 47, 56));
        assertThat(localTime).isEqualTo(LocalTime.of(15, 45, 0));

        localTime = dateAndTimeParserService.getFiveMinutesTime(LocalTime.of(15, 59, 56));
        assertThat(localTime).isEqualTo(LocalTime.of(15, 55, 0));

        localTime = dateAndTimeParserService.getFiveMinutesTime(LocalTime.of(15, 2, 56));
        assertThat(localTime).isEqualTo(LocalTime.of(15, 0, 0));
    }
}
