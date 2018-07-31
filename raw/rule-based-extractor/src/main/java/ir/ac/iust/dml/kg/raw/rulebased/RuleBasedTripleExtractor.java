/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Mohammad Abdous.
 */

package ir.ac.iust.dml.kg.raw.rulebased;

import edu.stanford.nlp.pipeline.Annotation;
import ir.ac.iust.dml.kg.raw.Normalizer;
import ir.ac.iust.dml.kg.raw.SentenceBranch;
import ir.ac.iust.dml.kg.raw.SentenceTokenizer;
import ir.ac.iust.dml.kg.raw.TextProcess;
import ir.ac.iust.dml.kg.raw.extractor.ResolvedEntityToken;
import ir.ac.iust.dml.kg.raw.services.access.entities.Rule;
import ir.ac.iust.dml.kg.raw.services.access.repositories.RuleRepository;
import ir.ac.iust.dml.kg.raw.triple.RawTriple;
import ir.ac.iust.dml.kg.raw.triple.RawTripleExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class RuleBasedTripleExtractor implements RawTripleExtractor {

  private static final Logger logger = LoggerFactory.getLogger(RuleBasedTripleExtractor.class);
  private final RuleRepository ruleDao;
  private ExtractTriple extractTriple;

  @Autowired
  public RuleBasedTripleExtractor(RuleRepository ruleDao) {
    this.ruleDao = ruleDao;
  }

  @PostConstruct
  public void init() {
    List<RuleAndPredicate> mainRuleAndPredicates = new ArrayList<>();
    List<Rule> rules = ruleDao.findAll();
    for (Rule rule : rules) {
      RuleAndPredicate ruleAndPredicate = new RuleAndPredicate();
      ruleAndPredicate.setRule(rule.getRule());
      ruleAndPredicate.setPredicate(rule.getPredicate());
      mainRuleAndPredicates.add(ruleAndPredicate);
    }
    extractTriple = new ExtractTriple(mainRuleAndPredicates);
    logger.info("rules loaded");
  }

  private TextProcess tp = new TextProcess();

  @Override
  public List<RawTriple> extract(String source, String version, String inputText) {
    inputText = SentenceBranch.summarize(Normalizer.removeBrackets(Normalizer.normalize(inputText)));
    List<RawTriple> result = new ArrayList<>();
    final List<String> lines = SentenceTokenizer.SentenceSplitterRaw(inputText);
    for (String line : lines) {
      Annotation annotation = new Annotation(line);
      tp.preProcess(annotation);
      result.addAll(extractTriple.extractTripleFromAnnotation(annotation));
    }
    return result;
  }

  @Override
  public List<RawTriple> extract(String source, String version, List<List<ResolvedEntityToken>> tokens) {
    List<RawTriple> result = new ArrayList<>();
    result.addAll(extractTriple.extractTripleFromAnnotation(tp.getAnnotationFromEntityTokens(tokens), tokens));
    return result;
  }

}
