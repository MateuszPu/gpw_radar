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

public class SMACrossoverTest {

    private List<Tickable> tickers;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void init() {
        tickers = ResourcesGetter.getTickers("kgh.txt");
    }

    @Test
    public void fastSMAcrossSlowerFromAboveTest() {
        List<Tickable> filteredTickers = tickers.stream().filter(ti -> ti.getDate().isBefore(LocalDate.of(2015, 11, 14))).collect(Collectors.toList());
        SMACrossover smaCrossover = new SMACrossover(filteredTickers, 15, 30);

        assertThat(smaCrossover.isFastSMACrossSlowerFromAbove()).isEqualTo(true);
        assertThat(smaCrossover.isFastSMACrossSlowerFromBellow()).isEqualTo(false);
    }

    @Test
    public void fastSMACrossSlowerFromAboveReversArgumentsTest() {
        List<Tickable> filteredTickers = tickers.stream().filter(ti -> ti.getDate().isBefore(LocalDate.of(2015, 11, 14))).collect(Collectors.toList());
        SMACrossover smaCrossover = new SMACrossover(filteredTickers, 30, 15);

        assertThat(smaCrossover.isFastSMACrossSlowerFromAbove()).isEqualTo(true);
        assertThat(smaCrossover.isFastSMACrossSlowerFromBellow()).isEqualTo(false);
    }

    @Test
    public void noCrossAtAllTest() {
        SMACrossover smaCrossover = new SMACrossover(tickers, 30, 15);

        assertThat(smaCrossover.isFastSMACrossSlowerFromAbove()).isEqualTo(false);
        assertThat(smaCrossover.isFastSMACrossSlowerFromBellow()).isEqualTo(false);
    }

    @Test
    public void fastSMAcrossSlowerFromBellowTest() {
        List<Tickable> filteredTickers = tickers.stream().filter(ti -> ti.getDate().isBefore(LocalDate.of(2015, 9, 23))).collect(Collectors.toList());
        SMACrossover smaCrossover = new SMACrossover(filteredTickers, 15, 30);

        assertThat(smaCrossover.isFastSMACrossSlowerFromAbove()).isEqualTo(false);
        assertThat(smaCrossover.isFastSMACrossSlowerFromBellow()).isEqualTo(true);
    }

    @Test
    public void invalidArgumentTest() {
        exception.expect(IllegalArgumentException.class);
        SMACrossover smaCrossover = new SMACrossover(tickers, 30, 30);
    }

}
