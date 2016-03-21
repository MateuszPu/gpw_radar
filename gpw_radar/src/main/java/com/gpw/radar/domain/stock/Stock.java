package com.gpw.radar.domain.stock;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gpw.radar.domain.User;
import com.gpw.radar.domain.enumeration.StockTicker;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Stock.
 */
@Entity
@Table(name = "STOCK")
public class Stock implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "idGenerator")
    @TableGenerator(table = "hibernate_sequences_table", name = "idGenerator", pkColumnName = "pk",
        valueColumnName = "value", pkColumnValue = "stock")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ticker", nullable = false)
    private StockTicker ticker;

    @Column(name = "stock_name")
    private String stockName;

    @Column(name = "stock_short_name")
    private String stockShortName;

    @JsonIgnore
	@ManyToMany(mappedBy = "stocks")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	private Set<User> users = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StockTicker getTicker() {
        return ticker;
    }

    public void setTicker(StockTicker ticker) {
        this.ticker = ticker;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getStockShortName() {
        return stockShortName;
    }

    public void setStockShortName(String stockShortName) {
        this.stockShortName = stockShortName;
    }

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((stockName == null) ? 0 : stockName.hashCode());
		result = prime * result + ((stockShortName == null) ? 0 : stockShortName.hashCode());
		result = prime * result + ((ticker == null) ? 0 : ticker.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Stock other = (Stock) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (stockName == null) {
			if (other.stockName != null)
				return false;
		} else if (!stockName.equals(other.stockName))
			return false;
		if (stockShortName == null) {
			if (other.stockShortName != null)
				return false;
		} else if (!stockShortName.equals(other.stockShortName))
			return false;
		if (ticker != other.ticker)
			return false;
		return true;
	}
}
