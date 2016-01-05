package pl.technical.analysis.indicators.trackers;

import org.junit.Test;
import pl.technical.analysis.ResourcesGetter;
import pl.technical.analysis.Tickable;

import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class EMAIndicatorTest {
    private List<Tickable> tickers = ResourcesGetter.getTickers("kgh.txt");
    EMAIndicator EMA;

    @Test
    public void SMA15Test() {
        EMA = new EMAIndicator(tickers, 15);
        assertThat(EMA.getLast()).isEqualTo(63.44);
}

    @Test
    public void SMA30Test() {
        EMA = new EMAIndicator(tickers, 30);
        assertThat(EMA.getLast()).isEqualTo(67.48);
    }
}
