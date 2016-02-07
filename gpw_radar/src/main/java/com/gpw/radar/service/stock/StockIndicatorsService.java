package com.gpw.radar.service.stock;

import org.springframework.stereotype.Service;
//import technical.analysis.indicator.TechnicalAnalysisIndicators;

@Service
public class StockIndicatorsService {

	//TODO: update it to use external lib
	public double[] calculateSlopeSimpleRegression(double[] closePrice, int period) {
//		double[] slopeSimpleRegressionArray = TechnicalAnalysisIndicators.calculateSlopRegression(closePrice, period);
		return new double[5];
	}
}
