package com.gpw.radar.domain.rss;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.rabbitmq.consumer.rss.news.RssType;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "STOCK_NEWS_MESSAGE")
public class NewsMessage {

    @Id
    @GenericGenerator(name = "seq_id", strategy = "com.gpw.radar.domain.generator.StringIdGenerator")
    @GeneratedValue(generator = "seq_id")
    private String id;

    @NotNull
    @Size(min = 1, max = 1000)
    @Column(length = 1000, nullable = false)
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", foreignKey = @ForeignKey(name = "FK_stock"))
    @JsonIgnore
    private Stock stock;

    @NotNull
    private String link;

    @NotNull
    @Column(name = "news_date_time", nullable = false)
    private LocalDateTime newsDateTime;

    @NotNull
    @Enumerated(EnumType.STRING)
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

}
