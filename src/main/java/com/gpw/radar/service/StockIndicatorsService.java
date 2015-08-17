package com.gpw.radar.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import technical.analysis.indicator.TechnicalAnalysisIndicators;


@Service
@Transactional
public class StockIndicatorsService {
	
	public double[] calculateSlopeSimpleRegression(double[] closePrice, int period){
		double[] slopeSimpleRegressionArray = TechnicalAnalysisIndicators.calculateSlopRegression(closePrice, period);		
		return slopeSimpleRegressionArray;
	}
}