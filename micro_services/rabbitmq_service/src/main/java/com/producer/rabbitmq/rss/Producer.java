package com.producer.rabbitmq.rss;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service("rssService")
public class Producer {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Value("${rss_reader_fanout_exchange}")
	private String fanoutNsme;

	@Value("${rss_reader_news_type_header}")
	private String newsType;

	@Value("${rss_reader_news_valid_link_header}")
	private String validLink;

	private final RabbitTemplate template;

	@Autowired
	public Producer(RabbitTemplate template) {
		this.template = template;
	}

	public void publish(String newsesJson, String rssChannelName, boolean validLink) {
		try {
			Message message = MessageBuilder.withBody(newsesJson.getBytes("UTF-8"))
					.setContentType(MessageProperties.CONTENT_TYPE_JSON)
					.setHeader(newsType, rssChannelName)
					.setHeader(this.validLink, validLink)
					.build();
			this.template.convertAndSend(fanoutNsme, "", message);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("Exception in {} with clause : {}", this.getClass().getName(), e.getCause());
		}
	}
}