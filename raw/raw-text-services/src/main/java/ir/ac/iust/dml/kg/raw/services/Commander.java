/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.services;

import ir.ac.iust.dml.kg.raw.extractor.EnhancedEntityExtractor;
import ir.ac.iust.dml.kg.raw.services.split.SplitLogic;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
class Commander {
  void processArgs(String[] args) throws IOException {
    final EnhancedEntityExtractor extractor = new EnhancedEntityExtractor();
    final Path path = Paths.get(args[1]);
    final Integer maxAmbiguities = args.length > 2 ? Integer.parseInt(args[2]) : 3;
    final Float contextDisambiguationThreshold = args.length > 3 ? Float.parseFloat(args[3]) : 0.0011f;
    switch (args[0]) {
      case "export":
        final String pattern = args.length > 4 ? args[4] : ".*\\.txt";
        extractor.exportFolder(path, pattern, maxAmbiguities, true,
            contextDisambiguationThreshold, true, true, true);
        break;
      case "exportWiki":
        extractor.exportWiki(path, maxAmbiguities, true,
            contextDisambiguationThreshold, true, true, true);
        break;
      case "conjCount":
        SplitLogic.INSTANCE.findConjunctions(path);
        break;
      case "conjAndDepCount":
        SplitLogic.INSTANCE.findConjunctionsAndDep(path);
        break;
    }
    System.exit(0);
  }
}
