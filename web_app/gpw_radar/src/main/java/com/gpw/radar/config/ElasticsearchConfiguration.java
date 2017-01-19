package com.gpw.radar.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@PropertySource("classpath:elasticsearch_config.properties")
@EnableElasticsearchRepositories(basePackages = "com.gpw.radar.elasticsearch")
public class ElasticsearchConfiguration {
}
