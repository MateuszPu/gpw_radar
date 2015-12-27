package com.gpw.radar.domain.rss;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gpw.radar.domain.User;
import com.gpw.radar.domain.chat.ChatMessage;
import com.gpw.radar.domain.enumeration.RssType;
import com.gpw.radar.domain.stock.Stock;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Entity
@Table(name = "NEWS_MESSAGE")
public class NewsMessage extends ChatMessage {

	@NotNull
	@Size(min = 1, max = 1000)
	@Column(length = 1000, nullable = false)
	@JsonIgnore
	private String message;

	@ManyToOne
	@JoinColumn(name = "stock_id", foreignKey = @ForeignKey(name = "FK_stock"))
	@JsonIgnore
	private Stock stock;

	@NotNull
	@JsonIgnore
	private String link;

	@NotNull
	@Enumerated(EnumType.STRING)
	private RssType type;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Stock getStock() {
		return stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public RssType getType() {
		return type;
	}

	public void setType(RssType type) {
		this.type = type;
	}

	@Override
	public String getChatMessage() {
		StringBuilder str = new StringBuilder();
		str.append("<a href=\"");
        str.append(getLink());
        str.append("\" target=\"_blank\">");
        str.append(getMessage());
        str.append("</a> ");
		return str.toString();
	}
}
