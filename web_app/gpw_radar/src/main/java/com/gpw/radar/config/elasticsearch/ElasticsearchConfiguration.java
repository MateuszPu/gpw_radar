package com.gpw.radar.config.elasticsearch;

import com.gpw.radar.config.Constants;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;


@Configuration
@PropertySource("classpath:${spring.profiles.active}_elasticsearch_config.properties")
@EnableElasticsearchRepositories(basePackages = "com.gpw.radar.elasticsearch")
@Profile({Constants.SPRING_PROFILE_PRODUCTION, Constants.SPRING_PROFILE_DEVELOPMENT})
public class ElasticsearchConfiguration {

}
