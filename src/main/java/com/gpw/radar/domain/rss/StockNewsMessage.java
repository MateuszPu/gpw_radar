package com.gpw.radar.domain.rss;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.gpw.radar.domain.chat.ChatMessage;
import com.gpw.radar.domain.stock.Stock;

@Entity
public class StockNewsMessage extends ChatMessage{

	
	@NotNull
	@Size(min = 1, max = 1000)
    @Column(length = 1000, nullable = false)
	private String message;
	
	@ManyToOne
	@NotNull
    @JoinColumn(name="stock_id", foreignKey = @ForeignKey(name="FK_stock"), nullable = false)
	private Stock stock;
	
	@NotNull
	private String link;

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

	@Override
	public String getChatMessage() {
		StringBuilder str = new StringBuilder();
		str.append("<a href=" + getLink() + "target=\"_blank\">Click!</a> ");
		str.append(getMessage());
		return str.toString();
	}
}
