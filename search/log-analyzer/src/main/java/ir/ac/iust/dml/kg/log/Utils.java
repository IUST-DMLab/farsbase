package ir.ac.iust.dml.kg.log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ali on 4/7/17.
 */
public class Utils {
    public static void persistSortedMap(Map<String, Long> map, String fileName) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName))) {
            for(Map.Entry<String,Long> pair : map.entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed()).collect(Collectors.toList()))
                writer.write(pair.getKey() + "\t" + pair.getValue() + "\n");
        }


    }
}
