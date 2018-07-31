/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.services.logic;

import edu.stanford.nlp.ling.TaggedWord;
import ir.ac.iust.dml.kg.raw.DependencyParser;
import ir.ac.iust.dml.kg.raw.Normalizer;
import ir.ac.iust.dml.kg.raw.SentenceBranch;
import ir.ac.iust.dml.kg.raw.extractor.DependencyInformation;
import ir.ac.iust.dml.kg.raw.extractor.EnhancedEntityExtractor;
import ir.ac.iust.dml.kg.raw.extractor.ResolvedEntityToken;
import ir.ac.iust.dml.kg.raw.services.tree.ParsingLogic;
import ir.ac.iust.dml.kg.raw.triple.RawTriple;
import ir.ac.iust.dml.kg.raw.triple.RawTripleExtractor;
import kotlin.Pair;
import org.maltparser.concurrent.graph.ConcurrentDependencyGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FKGfyLogic {
  private static final Logger LOGGER = LoggerFactory.getLogger(FKGfyLogic.class);
  private EnhancedEntityExtractor extractor;
  @Autowired
  private List<RawTripleExtractor> extractors;

  @SuppressWarnings("Duplicates")
  public List<List<ResolvedEntityToken>> fkgFy(String text) {
    if (extractor == null) extractor = new EnhancedEntityExtractor();
    final List<List<ResolvedEntityToken>> resolved = extractor.extract(
        SentenceBranch.summarize(Normalizer.removeBrackets(Normalizer.normalize(text))), false);
    extractor.disambiguateByContext(resolved, 3, 0, 0.00001f);
    extractor.resolveByName(resolved);
    extractor.resolvePronouns(resolved);
    return resolved;
  }

  private void addDepInfo(List<List<ResolvedEntityToken>> sentences) {
    for (List<ResolvedEntityToken> sentence : sentences) {
      final List<TaggedWord> words = new ArrayList<>();
      for (ResolvedEntityToken token : sentence) {
        words.add(new TaggedWord(token.getWord(), token.getPos()));
      }
      final ConcurrentDependencyGraph dep = DependencyParser.parse(words);
      if (dep != null)
        for (int i = 1; i < dep.nTokenNodes() + 1; i++)
          sentence.get(i - 1).setDep(new DependencyInformation(dep.getDependencyNode(i)));
    }
  }

  @SuppressWarnings("Duplicates")
  public List<RawTriple> extract(String text) {
    final List<RawTriple> allTriples = new ArrayList<>();
    try {
      final List<List<ResolvedEntityToken>> fkgfyed = fkgFy(text);
      for (RawTripleExtractor rawTripleExtractor : extractors) {
        try {
          final List<List<ResolvedEntityToken>> copy = ResolvedEntityToken.copySentences(fkgfyed);
          if (rawTripleExtractor instanceof ParsingLogic) addDepInfo(copy);
          final List<RawTriple> triples = rawTripleExtractor.extract("raw-text-ui", new Date().toString(), copy);
          if (triples != null) allTriples.addAll(triples);
        } catch (Throwable extractionError) {
          LOGGER.error("error in extractor", extractionError);
        }
      }
    } catch (Throwable throwable) {
      LOGGER.error("error in relation extraction", throwable);
    }
    allTriples.sort(Comparator.comparingDouble(RawTriple::getAccuracy));
    Collections.reverse(allTriples);
    return allTriples;
  }

  public List<String> related(String uri) {
    return weightedRelated(uri).parallelStream().map(Pair::component1).collect(Collectors.toList());
  }

  public List<Pair<String, Integer>> weightedRelated(String uri) {
    return extractor.relatedUris(uri, 0, 0.01f);
  }
}
