package pl.technical.analysis.indicators.trackers;

import org.assertj.core.data.Percentage;
import org.junit.Test;
import pl.technical.analysis.ResourcesGetter;
import pl.technical.analysis.Tickable;
import pl.technical.analysis.indicators.Indicator;

import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class WilliamsRIndicatorTest {

	private List<Tickable> tickers = ResourcesGetter.getTickers("kgh.txt");
    private Percentage percentage = Percentage.withPercentage(0.5);

    @Test
    public void WilliamsR10Test() {
    	Indicator wiliamsR = new WilliamsRIndicator(tickers, 10);
        assertThat(wiliamsR.getLast()).isCloseTo(68.34, percentage);
    }

    @Test
    public void WilliamsR15Test() {
    	Indicator wiliamsR = new WilliamsRIndicator(tickers, 15);
        assertThat(wiliamsR.getLast()).isCloseTo(76.15, percentage);
    }
}
