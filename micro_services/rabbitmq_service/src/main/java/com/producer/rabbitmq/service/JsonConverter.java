package com.producer.rabbitmq.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.producer.rabbitmq.config.jackson.CustomLocalDateSerializer;
import com.producer.rabbitmq.config.jackson.CustomLocalDateTimeSerializer;
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

	private ObjectMapper mapper;
	private JavaTimeModule javaTimeModule;

	public JsonConverter() {
		this.mapper = new ObjectMapper();
		this.javaTimeModule = new JavaTimeModule();
		this.javaTimeModule.addSerializer(LocalDateTime.class, new CustomLocalDateTimeSerializer(localDateTimeFormat));
		this.javaTimeModule.addSerializer(LocalDate.class, new CustomLocalDateSerializer(localDateFormat));
		this.mapper.registerModule(this.javaTimeModule);
		this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	}

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	public String convertToJson(List<T> objects) {
		String result = null;
		try {
			result = mapper.writeValueAsString(objects);
		} catch (JsonProcessingException e) {
			LOGGER.error("Exception in {} with clause : {}", this.getClass().getName(), e.getCause());
		}
		return result;
	}

	public String convertToJson(T object) {
		String result = null;
		try {
			result = mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			LOGGER.error("Exception in {} with clause : {}", this.getClass().getName(), e.getCause());
		}
		return result;
	}
}
