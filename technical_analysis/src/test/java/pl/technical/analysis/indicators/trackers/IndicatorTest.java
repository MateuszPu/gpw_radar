package pl.technical.analysis.indicators.trackers;

import org.junit.Test;
import pl.technical.analysis.ResourcesGetter;
import pl.technical.analysis.Tickable;
import pl.technical.analysis.indicators.trackers.sma.SMAIndicator;

import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;

/**
 * Created by mateusz on 14.06.2016.
 */
public class IndicatorTest {

    private List<Tickable> tickers = ResourcesGetter.getTickers("kgh.txt");

    @Test
    public void getPricesTest() {
        SMAIndicator sma15 = new SMAIndicator(tickers, 15);
        assertThat(sma15.getOpenPrice(1)).isEqualTo(65.47);
        assertThat(sma15.getMaxPrice(1)).isEqualTo(65.47);
        assertThat(sma15.getMinPrice(1)).isEqualTo(63.49);
        assertThat(sma15.getClosePrice(1)).isEqualTo(63.49);
        assertThat(sma15.getVolumePrice(1)).isEqualTo(504671);

        assertThat(sma15.getOpenPrice(3)).isEqualTo(62.52);
        assertThat(sma15.getMaxPrice(3)).isEqualTo(63.10);
        assertThat(sma15.getMinPrice(3)).isEqualTo(62.00);
        assertThat(sma15.getClosePrice(3)).isEqualTo(62.50);
        assertThat(sma15.getVolumePrice(3)).isEqualTo(668986);
    }
}
