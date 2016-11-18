package com.gpw.radar.service.builders;

import com.gpw.radar.domain.User;
import com.gpw.radar.domain.stock.Stock;

import java.util.HashSet;
import java.util.Set;

public class StockBuilder {

    private String id;
    private String ticker;
    private String stockName;
    private String stockShortName;
    private Set<User> users = new HashSet<>();

    public static StockBuilder stockBuilder() {
        return new StockBuilder();
    }

    public static StockBuilder sampleStock() {
        return stockBuilder().ticker("kgh").stockName("KGH name").stockShortName("KGH short name").users(new HashSet<>());
    }

    public StockBuilder id(String id) {
        this.id = id;
        return this;
    }

    public StockBuilder ticker(String ticker) {
        this.ticker = ticker;
        return this;
    }

    public StockBuilder stockName(String stockName) {
        this.stockName = stockName;
        return this;
    }

    public StockBuilder stockShortName(String stockShortName) {
        this.stockShortName = stockShortName;
        return this;
    }

    public StockBuilder users(Set<User> users) {
        this.users = users;
        return this;
    }

    public Stock build() {
        Stock stock = new Stock();
        stock.setId(this.id);
        stock.setTicker(this.ticker);
        stock.setStockShortName(this.stockShortName);
        stock.setStockName(this.stockName);
        stock.setUsers(this.users);
        return stock;
    }
}
