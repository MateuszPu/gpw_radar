package com.gpw.radar.domain.enumeration;

public enum RssType {
	RESULTS("http://biznes.pap.pl/pl/rss/6639"), CHALLENGE("http://biznes.pap.pl/pl/rss/6638"), RECOMMENDATIONS("http://biznes.pap.pl/pl/rss/6634"), PAP("http://biznes.pap.pl/pl/rss/6608"), EBI("http://biznes.pap.pl/pl/rss/6612"), ESPI("http://biznes.pap.pl/pl/rss/6614");

	private String url;

	RssType(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}
}
