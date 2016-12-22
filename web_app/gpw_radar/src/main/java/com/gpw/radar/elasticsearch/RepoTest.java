package com.gpw.radar.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface RepoTest extends ElasticsearchRepository<Test, String> {
}
