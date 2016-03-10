package com.gpw.radar.service.parser;

import com.gpw.radar.service.parser.web.UrlStreamsGetterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class DateAndTimeParserService {

    private final Logger logger = LoggerFactory.getLogger(DateAndTimeParserService.class);

    @Inject
    private UrlStreamsGetterService urlStreamsGetterService;

    //    private final DateTimeFormatter dtfTypeOne = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//    private final DateTimeFormatter dtfTypeTwo = DateTimeFormatter.ofPattern("yyyyMMdd");
//    private final DateTimeFormatter localTimeTypeOne = DateTimeFormatter.ofPattern("HH:mm:ss");
//    private final DateTimeFormatter localTimeTypeTwo = DateTimeFormatter.ofPattern("HHmmss");
//    private LocalDate date;
//    private LocalTime time;
    private final String yearRegex = "[1-9][0-9]{3}";
    private final String monthRegex = "[0-1][0-9]";
    private final String dayRegex = "[0-3][0-9]";
    private final String hourRegex = "([01]?[0-9]|2[0-3])";
    private final String minutesSecondsRegex = "[0-5][0-9]";

    private Map<String, DateTimeFormatter> dataTimeFormatterMap;
    private Map<String, DateTimeFormatter> localTimeFormatterMap;

    @PostConstruct
    private void init() {
        dataTimeFormatterMap = new HashMap<String, DateTimeFormatter>();
        dataTimeFormatterMap.put(yearRegex + "-" + monthRegex + "-" + dayRegex, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        dataTimeFormatterMap.put(yearRegex + monthRegex + dayRegex, DateTimeFormatter.ofPattern("yyyyMMdd"));

        localTimeFormatterMap = new HashMap<String, DateTimeFormatter>();
        localTimeFormatterMap.put(hourRegex + ":" + minutesSecondsRegex + ":" + minutesSecondsRegex, DateTimeFormatter.ofPattern("HH:mm:ss"));
        localTimeFormatterMap.put(hourRegex + minutesSecondsRegex + minutesSecondsRegex, DateTimeFormatter.ofPattern("HHmmss"));
    }

    public LocalDate parseLocalDateFromString(String date) {
        String key = dataTimeFormatterMap.keySet().stream().filter(k -> date.matches(k)).findFirst().get();
        return LocalDate.parse(date, dataTimeFormatterMap.get(key));
    }

    public LocalTime parseLocalTimeFromString(String time) {
        String key = localTimeFormatterMap.keySet().stream().filter(k -> time.matches(k)).findFirst().get();
        return LocalTime.parse(time, localTimeFormatterMap.get(key));
    }

    public LocalDate getLastDateWig20FromStooqWebsite() {
        String line = "";
        String cvsSplitBy = ",";
        LocalDate date = null;
        String url = "http://stooq.pl/q/l/?s=wig20&f=sd2t2ohlcv&h&e=csv";
        try (BufferedReader bufferedReader = new BufferedReader(urlStreamsGetterService.getInputStreamReaderFromUrl(url))){
            // skip first line as there are a headers
            bufferedReader.readLine();
            line = bufferedReader.readLine();
            String[] stockDetailsFromCsv = line.split(cvsSplitBy);
            date = parseLocalDateFromString(stockDetailsFromCsv[1]);
        } catch (IOException e) {
            logger.error("Error occurs: " + e.getMessage());
        }
        return date;
    }
}
