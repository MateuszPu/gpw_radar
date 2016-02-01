package pl.technical.analysis.indicators.trackers;

import org.junit.Test;
import pl.technical.analysis.ResourcesGetter;
import pl.technical.analysis.Tickable;
import pl.technical.analysis.indicators.Indicator;

import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class EMAIndicatorTest {
    private List<Tickable> tickers = ResourcesGetter.getTickers("kgh.txt");

    @Test
    public void EMA15Test() {
    	Indicator EMA = new EMAIndicator(tickers, 15);
        assertThat(EMA.getLast()).isEqualTo(63.44);
    }

    @Test
    public void EMA30Test() {
    	Indicator EMA = new EMAIndicator(tickers, 30);
        assertThat(EMA.getLast()).isEqualTo(67.48);
    }
}
