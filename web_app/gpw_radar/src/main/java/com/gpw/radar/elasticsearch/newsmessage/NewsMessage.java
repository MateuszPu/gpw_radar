package com.gpw.radar.elasticsearch.newsmessage;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gpw.radar.domain.util.date.CustomLocalDateTimeDeserializer;
import com.gpw.radar.domain.util.date.CustomLocalDateTimeSerializer;
import com.gpw.radar.elasticsearch.stockdetails.Stock;
import com.gpw.radar.rabbitmq.consumer.rss.news.RssType;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(indexName = "news_messages", type = "news", replicas = 0)
public class NewsMessage {

    public static final String DATE_TIME_FIELD_NAME = "newsDateTime";

    @Id
    private String id = UUID.randomUUID().toString();

    @Field(type = FieldType.String)
    private String message;

    @Field(type = FieldType.Object)
    private Stock stock;

    @Field(type = FieldType.String, index = FieldIndex.no)
    private String link;

    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd'T'HH:mm:ss", format = DateFormat.custom)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    private LocalDateTime newsDateTime;

    @Field(type = FieldType.String)
    private RssType type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public LocalDateTime getNewsDateTime() {
        return newsDateTime;
    }

    public void setNewsDateTime(LocalDateTime newsDateTime) {
        this.newsDateTime = newsDateTime;
    }

    public RssType getType() {
        return type;
    }

    public void setType(RssType type) {
        this.type = type;
    }

    public String transformToChatMessageContent() {
        StringBuilder str = new StringBuilder();
        str.append("[");
        str.append(this.newsDateTime);
        str.append("]");
        str.append("<a href=\"");
        str.append(this.link);
        str.append("\" target=\"_blank\">");
        str.append(this.message);
        str.append("</a>");
        return str.toString();
    }
}
