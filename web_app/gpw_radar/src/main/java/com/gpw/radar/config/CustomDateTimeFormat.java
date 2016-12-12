package com.gpw.radar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CustomDateTimeFormat {

    public static final String yearRegex = "[1-9][0-9]{3}";
    public static final String monthRegex = "[0-1][0-9]";
    public static final String dayRegex = "[0-3][0-9]";
    public static final String hourRegex = "([01]?[0-9]|2[0-3])";
    public static final String minutesSecondsRegex = "[0-5][0-9]";

    @Bean(name = "localDateTimeFormatter")
    public Map<String, DateTimeFormatter> localDateTimeFormatter() {
        Map<String, DateTimeFormatter> dataTimeFormatterMap = new HashMap<>();
        dataTimeFormatterMap.put(yearRegex + "-" + monthRegex + "-" + dayRegex, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        dataTimeFormatterMap.put(yearRegex + monthRegex + dayRegex, DateTimeFormatter.ofPattern("yyyyMMdd"));
        return dataTimeFormatterMap;
    }

    @Bean(name = "localTimeFormatter")
    public Map<String, DateTimeFormatter> localTimeFormatter() {
        Map<String, DateTimeFormatter> localTimeFormatterMap = new HashMap<>();
        localTimeFormatterMap.put(hourRegex + ":" + minutesSecondsRegex + ":" + minutesSecondsRegex, DateTimeFormatter.ofPattern("HH:mm:ss"));
        localTimeFormatterMap.put(hourRegex + minutesSecondsRegex + minutesSecondsRegex, DateTimeFormatter.ofPattern("HHmmss"));
        return localTimeFormatterMap;
    }
}
