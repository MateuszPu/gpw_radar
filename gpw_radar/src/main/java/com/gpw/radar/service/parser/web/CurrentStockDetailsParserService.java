package com.gpw.radar.service.parser.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

@Service
public class CurrentStockDetailsParserService {

    private final Logger logger = LoggerFactory.getLogger(CurrentStockDetailsParserService.class);

    public InputStreamReader getInputStreamReaderFromUrl(String url) {
        InputStreamReader inputStreamReader = null;
        try {
            URL urlContent = new URL(url);
            URLConnection urlConnection = urlContent.openConnection();
            inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
        } catch (IOException e) {
            logger.error("Error occurs: " + e.getMessage());
        }

        return inputStreamReader;
    }
}
