package com.gpw.radar.service.parser.web;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Service
public class GpwSiteDataParserService {

    public Document getDocumentFromInputStream(InputStream inputStream) {
        String htmlContent = getHtmlContent(inputStream);
        Document doc = Jsoup.parse(htmlContent);
        return doc;
    }

    private String getHtmlContent(InputStream inputStream) {
        String htmlContent = "";

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            htmlContent = bufferedReader.lines().filter(s -> s.startsWith("<table")).findAny().get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return htmlContent;
    }
}
