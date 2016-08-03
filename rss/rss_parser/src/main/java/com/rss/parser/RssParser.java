package com.rss.parser;

import com.rss.parser.model.GpwNews;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

public interface RssParser {

    List<GpwNews> parseBy(ZonedDateTime dateTime);
}
