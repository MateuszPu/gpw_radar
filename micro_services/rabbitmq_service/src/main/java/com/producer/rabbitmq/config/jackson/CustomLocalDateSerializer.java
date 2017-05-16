package com.producer.rabbitmq.config.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CustomLocalDateSerializer extends JsonSerializer<LocalDate> {

	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public CustomLocalDateSerializer(String localDateFormat) {
		if (!StringUtils.isEmpty(localDateFormat)) {
			formatter = DateTimeFormatter.ofPattern(localDateFormat);
		}
	}

	@Override
	public void serialize(LocalDate localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
		jsonGenerator.writeString(formatter.format(localDateTime));
	}
}
