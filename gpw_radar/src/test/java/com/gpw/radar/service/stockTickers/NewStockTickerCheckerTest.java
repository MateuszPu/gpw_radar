package com.gpw.radar.service.stockTickers;

import com.gpw.radar.service.auto.update.stockTickers.StockTickerUpdater;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class NewStockTickerCheckerTest {

    @Test
    public void stockDetailsSize() {
        List<String> tickersInDb = Arrays.asList("aaa", "bbb", "ccc", "ddd", "eee");
        List<String> tickersFromExternalSource = Arrays.asList("aaa", "bbb", "ccc", "fff");

        StockTickerUpdater updater = new StockTickerUpdater(null, null, null, null, null);
        Collection<String> result = updater.verifyNewStockTickers(tickersInDb, tickersFromExternalSource);

        assertThat(result).isEqualTo(Arrays.asList("fff"));
    }
}
