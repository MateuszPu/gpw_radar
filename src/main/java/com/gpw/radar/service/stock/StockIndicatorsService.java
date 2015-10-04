package com.gpw.radar.service.stock;

import org.springframework.stereotype.Service;

import technical.analysis.indicator.TechnicalAnalysisIndicators;

@Service
public class StockIndicatorsService {

	public double[] calculateSlopeSimpleRegression(double[] closePrice, int period) {
		double[] slopeSimpleRegressionArray = TechnicalAnalysisIndicators.calculateSlopRegression(closePrice, period);
		return slopeSimpleRegressionArray;
	}
}