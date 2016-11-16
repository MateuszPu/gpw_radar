package com.rss.rabbitmq.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rss.rabbitmq.config.jackson.CustomLocalDateSerializer;
import com.rss.rabbitmq.config.jackson.CustomLocalDateTimeSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class JsonConverter<T> {

    @Value("${local_date_format}")
    private String localDateFormat;

    @Value("${local_date_time_format}")
    private String localDateTimeFormat;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public String convertToJson(List<T> objects) {
        ObjectMapper mapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new CustomLocalDateTimeSerializer(localDateTimeFormat));
        javaTimeModule.addSerializer(LocalDate.class, new CustomLocalDateSerializer(localDateFormat));
        mapper.registerModule(javaTimeModule);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        String result = null;
        try {
            result = mapper.writeValueAsString(objects);
        } catch (JsonProcessingException e) {
            LOGGER.error("Exception in "
                    + this.getClass().getName()
                    + " with clause : "
                    + e.getCause());
        }
        return result;
    }
}
