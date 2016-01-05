package pl.technical.analysis.helpers;

import pl.technical.analysis.Tickable;

import java.util.List;
import java.util.stream.Collectors;

public class AscendingSortedTicks {

    private final List<Tickable> ticks;

    public AscendingSortedTicks(List<Tickable> ticks) {
        this.ticks = ticks.stream().sorted((d1, d2) -> d1.getDate().compareTo(d2.getDate())).collect(Collectors.toList());
    }

    public List<Tickable> getTicks() {
        return ticks;
    }

    public int size() {
        return ticks.size();
    }
}
