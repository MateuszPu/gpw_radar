package com.gpw.radar.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "T_STOCK_INDICATORS")
public class StockIndicators {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@JsonIgnore
	@OneToOne(mappedBy = "stockIndicators")
	private Stock stock;

	@Column(name = "slope_simple_regression_10")
	private double slopeSimpleRegression10Days;

	@Column(name = "slope_simple_regression_30")
	private double slopeSimpleRegression30Days;

	@Column(name = "slope_simple_regression_60")
	private double slopeSimpleRegression60Days;

	@Column(name = "slope_simple_regression_90")
	private double slopeSimpleRegression90Days;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Stock getStock() {
		return stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}

	public double getSlopeSimpleRegression10Days() {
		return slopeSimpleRegression10Days;
	}

	public void setSlopeSimpleRegression10Days(double slopeSimpleRegression10Days) {
		this.slopeSimpleRegression10Days = slopeSimpleRegression10Days;
	}

	public double getSlopeSimpleRegression30Days() {
		return slopeSimpleRegression30Days;
	}

	public void setSlopeSimpleRegression30Days(double slopeSimpleRegression30Days) {
		this.slopeSimpleRegression30Days = slopeSimpleRegression30Days;
	}

	public double getSlopeSimpleRegression60Days() {
		return slopeSimpleRegression60Days;
	}

	public void setSlopeSimpleRegression60Days(double slopeSimpleRegression60Days) {
		this.slopeSimpleRegression60Days = slopeSimpleRegression60Days;
	}

	public double getSlopeSimpleRegression90Days() {
		return slopeSimpleRegression90Days;
	}

	public void setSlopeSimpleRegression90Days(double slopeSimpleRegression90Days) {
		this.slopeSimpleRegression90Days = slopeSimpleRegression90Days;
	}
}
