package com.producer.rabbitmq;

import com.producer.rabbitmq.service.JsonConverter;
import com.rss.parser.model.GpwNews;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;

public class JsonConverterTest {

	@Test
	public void shouldConvertObjectToJson() {
		//given
		GpwNews objToConvert = new GpwNews(LocalDateTime.of(2016,1,2,3,15,10), "message", "link");
		JsonConverter<GpwNews> jsonConverter = new JsonConverter<>();

		//when
		String result = jsonConverter.convertToJson(objToConvert);

		//then
		Assert.assertEquals(result, "{\"newsDateTime\":\"2016-01-02T03:15:10\",\"message\":\"message\",\"link\":\"link\"}");
	}
}
