package pl.technical.analysis.indicators.trackers;

import static org.assertj.core.api.StrictAssertions.assertThat;

import java.util.List;

import org.junit.Test;

import pl.technical.analysis.ResourcesGetter;
import pl.technical.analysis.Tickable;
import pl.technical.analysis.indicators.Indicator;

public class WMAIndicatorTest {
    private List<Tickable> tickers = ResourcesGetter.getTickers("kgh.txt");

    @Test
    public void WMA15Test() {
    	Indicator WMA = new WMAIndicator(tickers, 15);
        assertThat(WMA.getLast()).isEqualTo(61.96);
    }

    @Test
    public void WMA30Test() {
    	Indicator WMA = new WMAIndicator(tickers, 30);
        assertThat(WMA.getLast()).isEqualTo(63.81);
    }
}
