package com.gpw.radar.elasticsearch;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "testowy_index", type = "type1", shards = 5, replicas = 0)
public class Test {

    @Id
    private String id;

    @Field(index = FieldIndex.not_analyzed, type = FieldType.String)
    private String dupa;

    public Test(String id, String dupa) {
        this.id = id;
        this.dupa = dupa;
    }
}
