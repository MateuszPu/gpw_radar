package technical.analysis.indicator;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.commons.math3.util.FastMath;

import java.util.Arrays;

/**
 * @author ppp
 *
 */
public class TechnicalAnalysisIndicators {

	/**
	 * @param closePriceData input array data from the newest to oldest
	 * @param period period of indicator
	 * @return array of Simple Moving Average indicator
	 */
	public static double[] calculateSMA(double[] closePriceData, int period) {
		double[] closePrice = closePriceData.clone();
		int length = closePrice.length - period;
		double[] SMA = new double[length];

		for (int i = 0; i < length; i++) {
			double[] subArray = Arrays.copyOfRange(closePrice, i, period + i);
			SMA[i] = StatUtils.mean(subArray);
		}
		return SMA;
	}

	/**
	 * @param closePriceData input array data from the newest to oldest
	 * @param period period of indicator
	 * @return array of Exponential Moving Average indicator
	 */
	public static double[] calculateEMA(double[] closePriceData, int period) {
		double[] closePrice = closePriceData.clone();
		int inputDataLength = closePrice.length;
		double[] EMA = new double[inputDataLength - period];
		double[] averageData = Arrays.copyOfRange(closePrice, inputDataLength - period, inputDataLength);
		double average = (float) StatUtils.mean(averageData);
		float k = 2.0f / (period + 1);

		EMA[inputDataLength - period - 1] = (closePrice[inputDataLength - period - 1] * k + average * (1 - k));
		for (int i = inputDataLength - period - 2; i >= 0; i--) {
			EMA[i] = closePrice[i] * k + EMA[i + 1] * (1 - k);
		}

		return EMA;
	}

	/**
	 * @param closePriceData input array data from the newest to oldest
	 * @param period period of indicator
	 * @return array of Simple Moving Average indicator
	 */
	public static double[] calculateWMA(double[] closePriceData, int period) {
		double[] closePrice = closePriceData.clone();
		int length = closePrice.length - period;
		Mean mean = new Mean();
		double[] weight = new double[period];

		int j = 1;
		for (int i = period - 1; i >= 0; i--) {
			weight[i] = j;
			j++;
		}

		double[] WMA = new double[length];
		for (int i = 0; i < length; i++) {
			double[] subArray = Arrays.copyOfRange(closePrice, i, period + i);
			WMA[i] = mean.evaluate(subArray, weight);
		}
		return WMA;
	}

	/**
	 * @param closePriceData input array data from the newest to oldest
	 * @param period period of indicator
	 * @return array of Rate of Change indicator
	 */
	public static double[] calculateROC(double[] closePriceData, int period) {
		double[] closePrice = closePriceData.clone();
		int length = closePrice.length - period;
		double[] roc = new double[length];
		for (int i = 0; i < length; i++) {
			double nPrice = closePrice[i];
			double nMinusParametrPrice = closePrice[i + period];
			roc[i] = (nPrice - nMinusParametrPrice) / nMinusParametrPrice * 100;
		}
		return roc;
	}

	/**
	 * @param closePriceData input array data of close prices from the newest to oldest
	 * @param highPriceData input array data of high prices from the newst to oldest
	 * @param lowPriceData input array data of low prices from the newest to oldest
	 * @param volumeData input array data of volume from the newest to oldest
	 * @return array of Volume-weighted Average Price indicator
	 */
	public static double[] calculateVWAP(double[] closePriceData, double[] highPriceData, double[] lowPriceData, int[] volumeData) {
		double[] closePrice = closePriceData.clone();
		int length = closePrice.length;
		double VWAP[] = new double[length];
		int lengthMinusOne = length - 1;
		VWAP[lengthMinusOne] = (closePrice[lengthMinusOne] + highPriceData[lengthMinusOne] + lowPriceData[lengthMinusOne]) / 3;
		double volumeMultiplyPrice = volumeData[lengthMinusOne]
				* (closePrice[lengthMinusOne] + highPriceData[lengthMinusOne] + lowPriceData[lengthMinusOne]) / 3;
		int totalVolume = volumeData[lengthMinusOne];

		for (int i = lengthMinusOne - 1; i >= 0; i--) {
			totalVolume += volumeData[i];
			volumeMultiplyPrice += volumeData[i] * (closePrice[i] + highPriceData[i] + lowPriceData[i]) / 3;
			VWAP[i] = volumeMultiplyPrice / totalVolume;
		}

		return VWAP;
	}

	/**
	 * @param closePriceData input array data of close prices from the newest to oldest
	 * @param highPriceData input array data of high prices from the newst to oldest
	 * @param lowPriceData input array data of low prices from the newest to oldest
	 * @param volumeData input array data of volume from the newest to oldest
	 * @param period period of indicator
	 * @return array of Chaikin Money Flow indicator
	 */
	public static double[] calculateChaikinMoneyFlow(double[] closePriceData, double[] highPriceData, double[] lowPriceData, int[] volumeData,
			int period) {
		double[] closePrice = closePriceData.clone();
		int length = closePrice.length;
		double[] moneyFlowVolume = new double[length];
		double[] chainkinMoneyFlow = new double[length - period];

		double[] moneyFlowMultiplier = calculateMoneyFlowMultiplier(closePrice, highPriceData, lowPriceData, length);

		for (int i = 0; i < length; i++) {
			moneyFlowVolume[i] = moneyFlowMultiplier[i] * volumeData[i];
		}

		for (int i = 0; i < length - period; i++) {
			double[] moneyFlowValues = Arrays.copyOfRange(moneyFlowVolume, i, period + i);
			double[] volumeValues = Arrays.copyOfRange(copyFromIntArray(volumeData), i, period + i);
			chainkinMoneyFlow[i] = StatUtils.sum(moneyFlowValues) / StatUtils.sum(volumeValues);
		}

		return chainkinMoneyFlow;
	}

	/**
	 * @param closePriceData input array data of close prices from the newest to oldest
	 * @param period period of indicator
	 * @return array of Relative Strength Index
	 */
	public static double[] calculateRSI(double[] closePriceData, int period) {
		double[] closePrice = closePriceData.clone();
		int length = closePrice.length;
		double[] negativeDays = new double[length - 1];
		double[] positiveDays = new double[length - 1];

		for (int i = 1; i < length; i++) {
			if (closePrice[i] - closePrice[i - 1] >= 0) {
				negativeDays[i - 1] = Math.abs(closePrice[i] - closePrice[i - 1]);
			} else {
				positiveDays[i - 1] = Math.abs(closePrice[i] - closePrice[i - 1]);
			}
		}
		int outputLength = length - period;

		double[] negativeRange = Arrays.copyOfRange(negativeDays, outputLength - 1, length - 1);
		double negativeAverage = StatUtils.mean(negativeRange);
		double[] positiveRange = Arrays.copyOfRange(positiveDays, outputLength - 1, length - 1);
		double positiveAverage = StatUtils.mean(positiveRange);

		double[] RSindicator = new double[outputLength];

		RSindicator[outputLength - 1] = positiveAverage / negativeAverage;
		int factor = period - 1;
		double positiveFactor = (positiveAverage * factor + positiveDays[outputLength - 2]) / period;
		double negativeFactor = (negativeAverage * factor + negativeDays[outputLength - 2]) / period;
		RSindicator[outputLength - 2] = positiveFactor / negativeFactor;
		for (int i = outputLength - 3; i >= 0; i--) {
			positiveFactor = (positiveFactor * factor + positiveDays[i]) / period;
			negativeFactor = (negativeFactor * factor + negativeDays[i]) / period;
			RSindicator[i] = positiveFactor / negativeFactor;
		}

		double[] RSI = new double[outputLength];

		for (int i = 0; i < outputLength; i++) {
			RSI[i] = 100 - (100 / (1 + RSindicator[i]));
		}
		return RSI;
	}

	/**
	 * @param closePriceData input array data of close prices from the newest to oldest
	 * @param shortPeriod short period of indicator
	 * @param longPeriod long period of indicator
	 * @return array of Moving Average Convergence/Divergence Oscillator indicator
	 */
	public static double[] calculateMACD(double[] closePriceData, int shortPeriod, int longPeriod) {
		double[] closePrice = closePriceData.clone();
		int length = closePrice.length - longPeriod;
		double[] MACD = new double[length];
		double[] shortEMA = calculateEMA(closePrice, shortPeriod);
		double[] longEMA = calculateEMA(closePrice, longPeriod);

		for (int i = 0; i < length; i++) {
			MACD[i] = shortEMA[i] - longEMA[i];
		}

		return MACD;
	}

	public static double[] calculateSlopRegression(double[] closePriceData, int period) {
		int length = closePriceData.length - period;
		double[] closePrice = closePriceData.clone();
		ArrayUtils.reverse(closePrice);
		double[] slopeRegression = new double[length];

		for (int i = 0; i < length; i++) {
			SimpleRegression simpleRegerssion = new SimpleRegression();
			double[] copyOfRange = Arrays.copyOfRange(closePrice, i, period + i);
			int index = 0;
			for (double element : copyOfRange) {
				simpleRegerssion.addData(index, element);
				index++;
			}
			slopeRegression[i] = simpleRegerssion.getSlope();
		}

		return slopeRegression;
	}

	/**
	 * @param closePriceData input array data from the newest to oldest
	 * @param period period of indicator
	 * @return array of Bollinger Lower Bands
	 */
	public static double[] calculateBollingerLowerBands(double[] closePriceData, int period) {
		double[] closePrice = closePriceData.clone();
		int length = closePrice.length - period;
		double[] standardDeviation = calculateStandardDeviationPop(closePrice, period, length);

		double[] SMA = calculateSMA(closePrice, period);
		double[] bollingerLowerBands = new double[length];

		for (int i = 0; i < length; i++) {
			bollingerLowerBands[i] = SMA[i] - 2 * standardDeviation[i];
		}

		return bollingerLowerBands;
	}

	/**
	 * @param closePriceData input array data from the newest to oldest
	 * @param period period of indicator
	 * @return array of Bollinger Higher Bands
	 */
	public static double[] calculateBollingerHigherBands(double[] closePriceData, int period) {
		double[] closePrice = closePriceData.clone();
		int length = closePrice.length - period;
		double[] standardDeviation = calculateStandardDeviationPop(closePrice, period, length);

		double[] SMA = calculateSMA(closePrice, period);
		double[] bollingerHigherBands = new double[length];

		for (int i = 0; i < length; i++) {
			bollingerHigherBands[i] = SMA[i] + 2 * standardDeviation[i];
		}

		return bollingerHigherBands;
	}

	private static double[] calculateStandardDeviationPop(double[] inputData, int period, int length) {
		double[] standardDevation = new double[length];

		for (int i = 0; i < length; i++) {
			double[] subArray = Arrays.copyOfRange(inputData, i, period + i);
			standardDevation[i] = FastMath.sqrt(StatUtils.populationVariance(subArray));
		}
		return standardDevation;
	}

	private static double[] copyFromIntArray(int[] source) {
		double[] dest = new double[source.length];
		for (int i = 0; i < source.length; i++) {
			dest[i] = source[i];
		}
		return dest;
	}

	private static double[] calculateMoneyFlowMultiplier(double[] closePriceData, double[] highPriceData, double[] lowPriceData, int length) {
		double[] moneyFlowMultiplier = new double[length];
		for (int i = 0; i < length; i++) {
			moneyFlowMultiplier[i] = ((closePriceData[i] - lowPriceData[i]) - (highPriceData[i] - closePriceData[i]))
					/ (highPriceData[i] - lowPriceData[i]);
		}
		return moneyFlowMultiplier;
	}
}
