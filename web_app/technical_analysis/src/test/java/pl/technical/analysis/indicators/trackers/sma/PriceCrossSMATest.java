package pl.technical.analysis.indicators.trackers.sma;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pl.technical.analysis.ResourcesGetter;
import pl.technical.analysis.Tickable;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class PriceCrossSMATest {

    private List<Tickable> tickers;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void init() {
        tickers = ResourcesGetter.getTickers("kgh.txt");
    }

    @Test
    public void fastSMAcrossSlowerFromAboveTest() {
        List<Tickable> filteredTickers = tickers.stream().filter(ti -> ti.getDate().isBefore(LocalDate.of(2015, 12, 23))).collect(Collectors.toList());
        PriceCrossSMA priceCrossSMA = new PriceCrossSMA(filteredTickers, 15);

        assertThat(priceCrossSMA.isPriceCrossSMAFromAbove()).isEqualTo(false);
        assertThat(priceCrossSMA.isPriceCrossSMAFromBellow()).isEqualTo(true);
    }

    @Test
    public void fastSMACrossSlowerFromAboveReversArgumentsTest() {
        List<Tickable> filteredTickers = tickers.stream().filter(ti -> ti.getDate().isBefore(LocalDate.of(2015, 9, 23))).collect(Collectors.toList());
        PriceCrossSMA priceCrossSMA = new PriceCrossSMA(filteredTickers, 15);

        assertThat(priceCrossSMA.isPriceCrossSMAFromAbove()).isEqualTo(true);
        assertThat(priceCrossSMA.isPriceCrossSMAFromBellow()).isEqualTo(false);
    }

}
