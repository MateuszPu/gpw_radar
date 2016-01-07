package pl.technical.analysis.indicators.trackers;

import org.junit.Test;
import pl.technical.analysis.ResourcesGetter;
import pl.technical.analysis.Tickable;
import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class RSIIndicatorTest {

    private List<Tickable> tickers = ResourcesGetter.getTickers("kgh.txt");

    @Test
    public void RSI15Test() {
        RSIIndicator RSI = new RSIIndicator(tickers, 15);
        assertThat(RSI.getLast()).isEqualTo(41.06);
    }

    @Test
    public void RSI9Test() {
        RSIIndicator RSI = new RSIIndicator(tickers, 9);
        assertThat(RSI.getLast()).isEqualTo(48.10);
    }
}
