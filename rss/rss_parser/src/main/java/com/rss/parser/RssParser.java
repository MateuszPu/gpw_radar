package com.rss.parser;

import com.rometools.rome.io.FeedException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

public interface RssParser {

    Optional<String> parseFrom(LocalDateTime dateTime) throws IOException, FeedException;
}
