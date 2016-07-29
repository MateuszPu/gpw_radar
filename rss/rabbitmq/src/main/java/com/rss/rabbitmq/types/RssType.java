package com.rss.rabbitmq.types;

public enum RssType {
    RESULTS("http://biznes.pap.pl/pl/rss/6639"), CHALLENGE("http://biznes.pap.pl/pl/rss/6638"),
    RECOMMENDATIONS("http://www.gpwinfostrefa.pl/GPWIS2/pl/rss/34"), PAP("http://biznes.pap.pl/pl/rss/6608"),
    EBI("http://www.gpwinfostrefa.pl/GPWIS2/pl/rss/10"), ESPI("http://www.gpwinfostrefa.pl/GPWIS2/pl/rss/9");

    private String url;

    RssType(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
