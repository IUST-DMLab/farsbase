/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.triple;

import ir.ac.iust.dml.kg.raw.triple.extractor.DirectoryScanner;
import ir.ac.iust.dml.kg.raw.triple.extractor.TripleExtractor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RawTripleExtractorRunner implements CommandLineRunner {

  private final Log logger = LogFactory.getLog(getClass());
  private final TripleExtractor tripleExtractor;
  private final DirectoryScanner scanner;

  @Autowired
  public RawTripleExtractorRunner(TripleExtractor tripleExtractor, DirectoryScanner scanner) {
    this.tripleExtractor = tripleExtractor;
    this.scanner = scanner;
  }

  @Override
  public void run(String... args) throws Exception {
    logger.info("ApplicationStartupRunner run method Started !!");
    if (args.length == 0) {
      scanner.enableScanning();
      return;
    }
    String folderPath = args[0];
    TripleExtractor.InputType type = args.length > 1
        ? TripleExtractor.InputType.valueOf(args[1])
        : TripleExtractor.InputType.Raw;
    tripleExtractor.writeTriplesToFiles(folderPath, type, args.length > 2 ? args[2] : null);
  }
}
