package com.gpw.radar.service;

import com.gpw.radar.config.CustomDateTimeFormat;
import com.gpw.radar.service.parser.DateAndTimeParserService;
import com.gpw.radar.service.parser.web.UrlStreamsGetterService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

public class DateAndTimeParserServiceTest {

    private DateAndTimeParserService dateAndTimeParserService;
    private UrlStreamsGetterService mockedUrlStreamsGetterService;

    @Before
    public void init() {
        mockService();
        CustomDateTimeFormat customDateTimeFormat = new CustomDateTimeFormat();
        dateAndTimeParserService = new DateAndTimeParserService(mockedUrlStreamsGetterService,
            customDateTimeFormat.localDateTimeFormatter(), customDateTimeFormat.localTimeFormatter());
    }

    private void mockService() {
        String exampleDataPath = "/stocks_data/daily/pl/wse_stocks/daily_stooq_site_kgh.csv";
        InputStream inputStreamOfStockDetails = getClass().getResourceAsStream(exampleDataPath);
        mockedUrlStreamsGetterService = Mockito.mock(UrlStreamsGetterService.class);
        when(mockedUrlStreamsGetterService.getInputStreamReaderFromUrl(anyObject())).thenReturn(new InputStreamReader(inputStreamOfStockDetails));
    }

    @Test
    public void getDateFromScvFileTest() {
        LocalDate result = dateAndTimeParserService.getLastDateWig20FromStooqWebsite();
        LocalDate expectedResult = LocalDate.of(2016, 3, 9);
        assertThat(result).isEqualTo(expectedResult);
    }

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

        localTime = dateAndTimeParserService.getFiveMinutesTime(LocalTime.of(16, 55, 56));
        assertThat(localTime).isEqualTo(LocalTime.of(16, 50, 0));
    }

    @Test
    public void getStringFromDate() {
        LocalDate input = LocalDate.of(2015, 5, 19);
        String expectedResult = "2015-05-19";
        String result = dateAndTimeParserService.getStringFromDate(input);
        assertThat(result).isEqualTo(expectedResult);
    }
}
