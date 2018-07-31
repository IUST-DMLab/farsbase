/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Mohammad Abdous.
 */

package ir.ac.iust.dml.kg.raw.triple.extractor;

import ir.ac.iust.dml.kg.raw.DependencyParser;
import ir.ac.iust.dml.kg.raw.Normalizer;
import ir.ac.iust.dml.kg.raw.SentenceBranch;
import ir.ac.iust.dml.kg.raw.SentenceTokenizer;
import ir.ac.iust.dml.kg.raw.extractor.EnhancedEntityExtractor;
import ir.ac.iust.dml.kg.raw.extractor.ResolvedEntityToken;
import ir.ac.iust.dml.kg.raw.services.tree.ParsingLogic;
import ir.ac.iust.dml.kg.raw.triple.RawTriple;
import ir.ac.iust.dml.kg.raw.triple.RawTripleExtractor;
import ir.ac.iust.dml.kg.raw.utils.PathWalker;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TripleExtractor {

  private static final Logger LOGGER = LoggerFactory.getLogger(SentenceTokenizer.class);
  private final List<RawTripleExtractor> extractors;

  @Autowired
  public TripleExtractor(List<RawTripleExtractor> extractors) {
    this.extractors = extractors;
  }

  @SuppressWarnings("unused")
  public enum InputType {
    Raw, Repository
  }

  public void writeTriplesToFiles(String folderPath, InputType inputType, String className) throws IOException {
    if (className != null) {
      List<RawTripleExtractor> toRemove = new ArrayList<>();
      for (RawTripleExtractor extractor : extractors) {
        if (!extractor.getClass().getSimpleName().toLowerCase().equals(className.toLowerCase()))
          toRemove.add(extractor);
      }
      extractors.removeAll(toRemove);
    }

    final Path baseFolder = Paths.get(folderPath);
    final List<Path> fileList = PathWalker.INSTANCE.getPath(baseFolder,
        inputType == InputType.Raw ? null : ".*\\.json");
    final Path outputFolder = baseFolder.resolve("output");
    if (!Files.exists(outputFolder)) Files.createDirectories(outputFolder);

    int numberOfSentences = 0;
    final Map<Class, Integer> numberOfExtractedTriples = new HashMap<>();
    final Long extractionStart = System.currentTimeMillis();
    int lastLogNumberOfSentences = 0;
    for (Path file : fileList) {
      List<RawTriple> allFileTriples = new ArrayList<>();
      Object input;
      try {
        if (inputType == InputType.Raw) {
          final String texts = FileUtils.readFileToString(file.toFile(), "UTF-8");
          //String outputText = rfinder.getAnnotationTextAfterCoref(fileRawText);
          final List<String> sentences = SentenceTokenizer.SentenceSplitterRaw(texts);
          numberOfSentences += sentences.size();
          input = texts;
        } else {

          final List<List<ResolvedEntityToken>> tokens = EnhancedEntityExtractor.importFromFile(file);
          assert tokens != null;
          numberOfSentences += tokens.size();
          input = tokens;
        }
      } catch (Throwable th) {
        LOGGER.error("error in file reading " + file.toAbsolutePath());
        continue;
      }

      if (numberOfSentences - lastLogNumberOfSentences > 100) {
        showStats(numberOfSentences, numberOfExtractedTriples, extractionStart);
        lastLogNumberOfSentences = numberOfSentences;
      }

      for (RawTripleExtractor rawTripleExtractor : extractors) {
        try {
          final List<RawTriple> triples;
          if (input instanceof String)
            triples = rawTripleExtractor.extract(null, null,
                SentenceBranch.summarize(Normalizer.removeBrackets((String) input)));
          else {
            //noinspection unchecked
            final List<List<ResolvedEntityToken>> sentences = (List<List<ResolvedEntityToken>>) input;
            final List<List<ResolvedEntityToken>> copy = ResolvedEntityToken.copySentences(sentences);
            if (rawTripleExtractor instanceof ParsingLogic) DependencyParser.addDependencyParseSentences(copy, false);
            triples = rawTripleExtractor.extract(null, null, copy);
          }
          if (!triples.isEmpty()) {
            final Integer oldValue = numberOfExtractedTriples.get(rawTripleExtractor.getClass());
            final int newValue = (oldValue == null ? 0 : oldValue) + triples.size();
            numberOfExtractedTriples.put(rawTripleExtractor.getClass(), newValue);
            allFileTriples.addAll(triples);
          }
        } catch (Exception e) {
          LOGGER.trace("error in extracting triples from " + file.toAbsolutePath(), e);
        }
      }
      if (!allFileTriples.isEmpty())
        ExtractorUtils.writeTriples(outputFolder.resolve(file.getFileName() + ".json"), allFileTriples);
    }
    ExtractorUtils.markExtraction(outputFolder, extractionStart);
    showStats(numberOfSentences, numberOfExtractedTriples, extractionStart);
    System.exit(0);
  }

  private void showStats(int numberOfSentences, Map<Class, Integer> numberOfExtractedTriples, long startTime) {
    LOGGER.warn(String.format("%6d sentences has been processed in %d mili-seconds",
        numberOfSentences, (System.currentTimeMillis() - startTime)));
    numberOfExtractedTriples.forEach((key, value) ->
        LOGGER.warn(String.format("Number of extracted triples from %s is %d.", key.getSimpleName(), value)));
  }
}