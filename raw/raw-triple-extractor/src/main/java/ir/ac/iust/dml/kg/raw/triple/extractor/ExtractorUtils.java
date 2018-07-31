/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.triple.extractor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ir.ac.iust.dml.kg.raw.triple.RawTriple;
import ir.ac.iust.dml.kg.raw.triple.RawTripleExporter;
import ir.ac.iust.dml.kg.raw.utils.Module;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

class ExtractorUtils {

  private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

  @SuppressWarnings("UnusedReturnValue")
  static boolean writeTriples(Path path, List<RawTriple> triples) {
    RawTripleExporter rawTripleExporter;
    try {
      rawTripleExporter = new RawTripleExporter(path);
      rawTripleExporter.writeTripleList(triples);
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  @SuppressWarnings("UnusedReturnValue")
  static boolean markExtraction(Path outputFolder, long extractionStart) {
    try {
      Long extractionEnd = System.currentTimeMillis();
      Info info = new Info();
      info.setExtractionStart(String.valueOf(extractionStart));
      info.setExtractionEnd(extractionEnd.toString());
      info.setModule(Module.raw_dependency_pattern.toString());
      FileUtils.write(outputFolder.resolve("info.json").toFile(), gson.toJson(info), "UTF-8", false);
      return true;
    } catch (IOException e) {
      return false;
    }
  }
}
