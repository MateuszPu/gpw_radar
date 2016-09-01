package pl.technical.analysis.helper;

import org.junit.Before;
import org.junit.Test;
import pl.technical.analysis.Tick;
import pl.technical.analysis.Tickable;
import pl.technical.analysis.helpers.AscendingSortedTicks;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class AscendingSortedTicksTest {

    private List<Tickable> tickers = new ArrayList<>();
    private AscendingSortedTicks ascendingSortedTicks;

    @Before
    public void init() {
        tickers.add(new Tick(LocalDate.of(2015, 1, 1), 0, 0, 0, 0, 0, 0));
        tickers.add(new Tick(LocalDate.of(2015, 12, 3), 0, 0, 0, 0, 0, 0));
        tickers.add(new Tick(LocalDate.of(2015, 12, 7), 0, 0, 0, 0, 0, 0));
        tickers.add(new Tick(LocalDate.of(2015, 11, 5), 0, 0, 0, 0, 0, 0));
        tickers.add(new Tick(LocalDate.of(2015, 5, 23), 0, 0, 0, 0, 0, 0));

        ascendingSortedTicks = new AscendingSortedTicks(tickers);
    }

    @Test
    public void testOrder() {
        assertThat(ascendingSortedTicks.getTicks().get(0).getDate()).isEqualTo(LocalDate.of(2015,1,1));
    }
}
