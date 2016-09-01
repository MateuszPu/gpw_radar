package com.gpw.radar.service.parser.web;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

@Service
public class UrlStreamsGetterService {

    private final Logger logger = LoggerFactory.getLogger(UrlStreamsGetterService.class);

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

    public InputStream getInputStreamFromUrl(String url) {
        InputStream inputStream = null;

        try {
            URL urlContent = new URL(url);
            inputStream = urlContent.openStream();
        } catch (IOException e) {
            logger.error("Error occurs: " + e.getMessage());
        }
        return inputStream;
    }

    public Document getDocFromUrl(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            logger.error("Error occurs: " + e.getMessage());
        }
        return doc;
    }
}
