package pl.technical.analysis.indicators.trackers;

import org.junit.Test;
import pl.technical.analysis.ResourcesGetter;
import pl.technical.analysis.Tickable;

import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class WMAIndicatorTest {
    private List<Tickable> tickers = ResourcesGetter.getTickers("kgh.txt");

    @Test
    public void WMA15Test() {
        WMAIndicator WMA = new WMAIndicator(tickers, 15);
        assertThat(WMA.getLast()).isEqualTo(61.96);
    }

    @Test
    public void WMA30Test() {
        WMAIndicator WMA = new WMAIndicator(tickers, 30);
        assertThat(WMA.getLast()).isEqualTo(63.81);
    }
}
