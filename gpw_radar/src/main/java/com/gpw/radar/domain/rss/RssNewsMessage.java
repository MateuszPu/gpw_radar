//package com.gpw.radar.domain.rss;
//
//import com.gpw.radar.domain.enumeration.RssType;
//import com.gpw.radar.domain.stock.Stock;
//
//import java.time.ZonedDateTime;
//
//public final class RssNewsMessage {
//    private String message;
//    private String link;
//    private RssType type;
//    private Stock stock;
//    private ZonedDateTime date;
//
//    public RssNewsMessage(String message, RssType type, String link, ZonedDateTime date ) {
//        this.message = message;
//        this.type = type;
//        this.link = link;
//    }
//
//    public RssNewsMessage(String message, RssType type, String link, ZonedDateTime date, Stock stock) {
//        this.message = message;
//        this.type = type;
//        this.link = link;
//        this.stock = stock;
//    }
//
//    public RssType getType() {
//        return type;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//
//    public String getLink() {
//        return link;
//    }
//
//    public Stock getStock() {
//        return stock;
//    }
//}
