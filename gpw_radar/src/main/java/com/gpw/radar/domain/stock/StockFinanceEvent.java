package com.gpw.radar.domain.stock;

import com.gpw.radar.service.util.RandomUtil;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;


/**
 * A StockFinanceEvent.
 */
@Entity
@Table(name = "STOCK_FINANCE_EVENT")
public class StockFinanceEvent implements Serializable {

    @Id
    private String id = RandomUtil.generateId();

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @NotNull
    @Column(name = "message", nullable = false)
    private String message;

    @ManyToOne
    @JoinColumn(name="stock_id", foreignKey = @ForeignKey(name="FK_finance_event_to_stock"))
    private Stock stock;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

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
}
