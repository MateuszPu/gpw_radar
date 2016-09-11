package com.stock.details.updater.parser.gpw;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class HtmlParser {

    public Elements getTableRowsContentFromWeb() {
        String htmlContent = getHtmlContent();
        org.jsoup.nodes.Document doc = Jsoup.parse(htmlContent);
        Elements tableRows = doc.select("tr");
        return tableRows;
    }

    public String getHtmlContent() {
        StringBuilder htmlContent = new StringBuilder();
        try (BufferedReader bufferedReader = getBufferedReaderFromUrl()) {
            bufferedReader.lines().forEach(e -> System.out.println(htmlContent.append(e.toString())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String html = htmlContent.toString();
        return html.substring(html.indexOf("<table"), html.indexOf("/table"));
    }

    public BufferedReader getBufferedReaderFromUrl() throws IOException {
        URL url = new URL("https://www.gpw.pl/ajaxindex.php?action=GPWQuotations&start=showTable&tab=all&lang=PL&full=1");
        URLConnection urlConnection = url.openConnection();
        BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        return br;
    }
}
