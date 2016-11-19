package com.stock.details.updater.parser.gpw;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HtmlParser {

    private static final DateTimeFormatter dtfType = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public Elements getTableRowsContentFromWeb() {
        String htmlContent = getHtmlContent();
        Document doc = Jsoup.parse(htmlContent);
        Elements tableRows = doc.select("tr");
        return tableRows;
    }

    private String getHtmlContent() {
        StringBuilder htmlContent = new StringBuilder();
        try (BufferedReader bufferedReader = getBufferedReaderFromUrl()) {
            bufferedReader.lines().forEach(e -> htmlContent.append(e.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String html = htmlContent.toString();
        return html.substring(html.indexOf("<table"), html.indexOf("/table"));
    }

    private BufferedReader getBufferedReaderFromUrl() throws IOException {
        URL url = new URL("https://www.gpw.pl/ajaxindex.php?action=GPWQuotations&start=showTable&tab=all&lang=PL&full=1");
        URLConnection urlConnection = url.openConnection();
        BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        return br;
    }

    public LocalDate getCurrentDateOfStockDetails(Document doc) throws IOException {
        Elements el = doc.select("div[class=\"colFL\"]");
        LocalDate date = LocalDate.parse(el.first().text(), dtfType);
        return date;
    }

    public LocalDate getCurrentDateOfStockDetails() throws IOException {
        Document doc = Jsoup.connect("http://www.gpw.pl/akcje_i_pda_notowania_ciagle_pelna_wersja#all").get();
        return getCurrentDateOfStockDetails(doc);
    }
}
