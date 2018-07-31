/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.rulebased;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class RuleFileLoader {
  public static ExtractTriple load(String path) {
    try {
      final List<RuleAndPredicate> rules = new ArrayList<>();
      final List<String> lines = Files.readAllLines(Paths.get(path));
      lines.forEach(line -> {
            final String[] splits = line.split("\\s+~\\s+");
            rules.add(new RuleAndPredicate(splits[1], splits[0]));
          }
      );
      return new ExtractTriple(rules);
    } catch (IOException e) {
      return null;
    }
  }
}
