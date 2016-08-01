package com.rss.parser;

import com.rometools.rome.io.FeedException;
import com.rss.parser.model.GpwNews;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RssParser {

    List<GpwNews> parseBy(LocalDateTime dateTime);
}
