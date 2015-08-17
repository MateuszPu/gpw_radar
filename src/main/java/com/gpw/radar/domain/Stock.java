package com.gpw.radar.domain;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gpw.radar.domain.enumeration.StockTicker;

/**
 * A Stock.
 */
@Entity
@Table(name = "STOCK")
public class Stock implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
	private Set<User> users = new HashSet<>();
    
//    @JsonIgnore
//	@OneToMany(mappedBy = "stock", cascade = CascadeType.ALL)
//	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
//	private Set<StockDetails> stockDetails = new HashSet<>();

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

//	public Set<StockDetails> getStockDetails() {
//		return stockDetails;
//	}
//
//	public void setStockDetails(Set<StockDetails> stockDetails) {
//		this.stockDetails = stockDetails;
//	}
}
