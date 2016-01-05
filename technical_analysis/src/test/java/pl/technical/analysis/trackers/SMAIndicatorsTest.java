package pl.technical.analysis.trackers;

import org.junit.Before;
import org.junit.Test;
import pl.technical.analysis.ResourcesGetter;
import pl.technical.analysis.Tick;
import pl.technical.analysis.Tickable;
import pl.technical.analysis.indicators.trackers.SMAIndicators;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.StrictAssertions.assertThat;


public class SMAIndicatorsTest {

    private List<Tickable> tickers = ResourcesGetter.getTickers("kgh.txt");
    SMAIndicators SMA;

    @Before
    public void init() {
        SMA = new SMAIndicators(tickers, 15);
    }

    @Test
    public void SMATest() {
        assertThat(SMA.getLast()).isEqualTo(61.2);
    }
}
