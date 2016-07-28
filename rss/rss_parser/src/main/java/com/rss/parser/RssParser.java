package com.rss.parser;

import java.time.LocalDateTime;

public interface RssParser {

    String parseFrom(LocalDateTime localDateTime, String url);
}
