package pl.technical.analysis.indicators.trackers;

import org.junit.Test;
import pl.technical.analysis.ResourcesGetter;
import pl.technical.analysis.Tickable;
import pl.technical.analysis.indicators.Indicator;

import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class ROCIndicatorTest {
	private List<Tickable> tickers = ResourcesGetter.getTickers("kgh.txt");

    @Test
    public void ROC9Test() {
        Indicator ROC = new ROCIndicator(tickers, 9);
        assertThat(ROC.getLast()).isEqualTo(5.29);
    }

    @Test
    public void ROC15Test() {
    	Indicator ROC = new ROCIndicator(tickers, 15);
        assertThat(ROC.getLast()).isEqualTo(0.87);
    }
}
