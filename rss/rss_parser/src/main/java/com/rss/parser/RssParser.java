package com.rss.parser;

import com.rss.parser.model.GpwNews;

import java.time.LocalDateTime;
import java.util.List;

public interface RssParser {

    List<GpwNews> parseBy(LocalDateTime dateTime);
}
