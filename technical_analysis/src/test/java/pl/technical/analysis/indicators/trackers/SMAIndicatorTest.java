package pl.technical.analysis.indicators.trackers;

import org.junit.Before;
import org.junit.Test;
import pl.technical.analysis.ResourcesGetter;
import pl.technical.analysis.Tickable;

import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;


public class SMAIndicatorTest {

    private List<Tickable> tickers = ResourcesGetter.getTickers("kgh.txt");
    SMAIndicator SMA;

    @Test
    public void SMA15Test() {
        SMA = new SMAIndicator(tickers, 15);
        assertThat(SMA.getLast()).isEqualTo(61.2);
    }

    @Test
    public void SMA30Test() {
        SMA = new SMAIndicator(tickers, 30);
        assertThat(SMA.getLast()).isEqualTo(66.93);
    }
}
