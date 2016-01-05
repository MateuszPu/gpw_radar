package pl.technical.analysis;

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

public class ResourcesGetter {

    public static List<Tickable> getTickers(String filePath){
        List<Tickable> tickers = new ArrayList<>();
        ClassLoader classLoader = ResourcesGetter.class.getClassLoader();
        File file = new File(classLoader.getResource(filePath).getFile());
        try {
            Stream<String> lines = Files.lines(Paths.get(file.toURI()));
            tickers = lines.map(e -> mapToTick(e)).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tickers;
    }

    private static Tick mapToTick(String e) {
        String seperator = ",";
        String[] elements = e.split(seperator);

        DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate date = LocalDate.parse(elements[0], DATE_FORMAT);
        return new Tick(date, Double.valueOf(elements[1]), Double.valueOf(elements[2]), Double.valueOf(elements[3]), Double.valueOf(elements[4]), Long.valueOf(elements[5]), 0);
    }
}
