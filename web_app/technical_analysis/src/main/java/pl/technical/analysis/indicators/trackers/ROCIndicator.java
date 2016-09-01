package pl.technical.analysis.indicators.trackers;

import pl.technical.analysis.Tickable;
import pl.technical.analysis.indicators.Indicator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Relative strength index indicator.
 * <p>
 * More information regards to indicator:
 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:rate_of_change_roc_and_momentum
 */
public class ROCIndicator extends Indicator {

	public ROCIndicator(List<Tickable> ticks, int period) {
		super(ticks, period);
	}

	@Override
	protected void calculateIndicator() {
		List<BigDecimal> closesPrices = super.getClosePrices();
		
		for (int i = super.period; i < closesPrices.size(); i++) {
			double rocIndicator = 0;
			BigDecimal periodAgoClose = closesPrices.get(i-super.period);
			BigDecimal currentClose = closesPrices.get(i);
			BigDecimal differences = currentClose.subtract(periodAgoClose);
			
			rocIndicator = (differences.divide(periodAgoClose, 4, RoundingMode.HALF_UP)).multiply(super.hundred).doubleValue();
			super.indicators.add(rocIndicator);
		}
	}

}
