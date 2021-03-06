package pl.technical.analysis.indicators.trackers.sma;

import org.junit.Test;
import pl.technical.analysis.ResourcesGetter;
import pl.technical.analysis.Tickable;
import pl.technical.analysis.indicators.Indicator;
import pl.technical.analysis.indicators.trackers.sma.SMAIndicator;

import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;


public class SMAIndicatorTest {

    private List<Tickable> tickers = ResourcesGetter.getTickers("kgh.txt");

    @Test
    public void SMA15Test() {
    	Indicator SMA = new SMAIndicator(tickers, 15);
        assertThat(SMA.getLast()).isEqualTo(61.2);
    }

    @Test
    public void SMA30Test() {
    	Indicator SMA = new SMAIndicator(tickers, 30);
        assertThat(SMA.getLast()).isEqualTo(66.93);
    }
}
