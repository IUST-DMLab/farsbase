/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Mohammad Abdous.
 */

package ir.ac.iust.dml.kg.raw.rulebased;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.tokensregex.Env;
import edu.stanford.nlp.ling.tokensregex.SequenceMatchResult;
import edu.stanford.nlp.ling.tokensregex.TokenSequenceMatcher;
import edu.stanford.nlp.ling.tokensregex.TokenSequencePattern;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import ir.ac.iust.dml.kg.raw.SentenceTokenizer;
import ir.ac.iust.dml.kg.raw.extractor.EnhancedEntityExtractor;
import ir.ac.iust.dml.kg.raw.extractor.IobType;
import ir.ac.iust.dml.kg.raw.extractor.ResolvedEntityToken;
import ir.ac.iust.dml.kg.raw.extractor.ResolvedEntityTokenResource;
import ir.ac.iust.dml.kg.raw.triple.RawTriple;
import ir.ac.iust.dml.kg.raw.utils.URIs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ExtractTriple {

  private static final Logger LOGGER = LoggerFactory.getLogger(SentenceTokenizer.class);
  private final List<RuleAndPredicate> rules;
  private EnhancedEntityExtractor enhancedEntityExtractor;

  public ExtractTriple(List<RuleAndPredicate> rules) {
    this.rules = rules;
    List<RuleAndPredicate> notCompiledRules = new ArrayList<>();
    Env environment = TokenSequencePattern.getNewEnv();
    for (RuleAndPredicate rule : rules) {
      try {
        final TokenSequencePattern compiled = TokenSequencePattern.compile(environment, rule.getRule());
        rule.setPattern(compiled);
      } catch (Throwable ignored) {
        // compile error
        notCompiledRules.add(rule);
      }
    }
    enhancedEntityExtractor = new EnhancedEntityExtractor();
    this.rules.removeAll(notCompiledRules);
  }

  private List<List<ResolvedEntityToken>> fkgFy(String text) {
    if (enhancedEntityExtractor == null) enhancedEntityExtractor = new EnhancedEntityExtractor();
    final List<List<ResolvedEntityToken>> resolved = enhancedEntityExtractor.extract(text);
    enhancedEntityExtractor.disambiguateByContext(resolved, 3, 0.0001f);
    enhancedEntityExtractor.resolveByName(resolved);
    enhancedEntityExtractor.resolvePronouns(resolved);
    return resolved;
  }

  private List<RawTriple> extractTripleFromSentence(CoreMap sentence, List<ResolvedEntityToken> preResolvedToken) {
    List<RawTriple> triples = new ArrayList<>();
    String sentenceText = sentence.get(CoreAnnotations.TextAnnotation.class);
    // List<MatchedResource> result = client.match(sentenceText);

    if (preResolvedToken == null) {
      List<List<ResolvedEntityToken>> lists = fkgFy(sentenceText);
      annotateEntityType(sentence, lists.get(0));
    } else annotateEntityType(sentence, preResolvedToken);
    for (RuleAndPredicate rule : rules) {
      List<CoreLabel> StanfordTokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
      TokenSequenceMatcher matcher = rule.getPattern().getMatcher(StanfordTokens);
      while (matcher.find()) {
        RawTriple triple = getTriple(matcher);
        triple.setPredicate(rule.getPredicate());
        triple.setRawText(sentence.get(CoreAnnotations.TextAnnotation.class));
        triple.setModule("RuleBased");
        triple.setAccuracy(0.95);
        triples.add(triple);
      }
    }
    return triples;
  }

  private void annotateEntityType(CoreMap sentence, List<ResolvedEntityToken> matchedResources) {
    List<CoreLabel> coreLabels = sentence.get(CoreAnnotations.TokensAnnotation.class);
    for (CoreLabel coreLabel : coreLabels)
      coreLabel.setNER("O");
    if (matchedResources == null)
      return;
    try {
      for (int i = 0; i < matchedResources.size(); i++) {
        ResolvedEntityToken matchedResource = matchedResources.get(i);
        if (matchedResource.getResource() != null) {
          String matchedResourceType = getMatchedResourceType(matchedResource);
          if (matchedResource.getIobType().equals(IobType.Beginning))
            coreLabels.get(i).setNER("B_" + matchedResourceType);
          else if (matchedResource.getIobType().equals(IobType.Inside))
            coreLabels.get(i).setNER("I_" + matchedResourceType);
          coreLabels.get(i).set(ResourceTagAnnotation.class, matchedResource.getResource().getIri());
        }
      }
    } catch (Exception ex) {
      LOGGER.trace("Error in annotateEntityType method", ex);
    }

  }

  private String getMatchedResourceType(ResolvedEntityToken matchedResource) {
    StringBuilder builder = new StringBuilder();
    List<ResolvedEntityTokenResource> ambiguities = matchedResource.getAmbiguities();
    for (int i = 0; i < ambiguities.size(); i++) {
      ResolvedEntityTokenResource resolvedEntityTokenResource = ambiguities.get(i);
      if (i <= 1 && resolvedEntityTokenResource.getMainClass() != null)
        builder.append(resolvedEntityTokenResource.getMainClass()
            .replace("http://fkg.iust.ac.ir/ontology/", "")).append(',');
    }
    for (String classStr : matchedResource.getResource().getClasses()) {

      builder.append(classStr.replace("http://fkg.iust.ac.ir/ontology/", "")).append(',');
    }

    //if (builder.length() > 0) builder.setLength(builder.length() - 1);
    builder.append(matchedResource.getResource().getMainClass()
        .replace("http://fkg.iust.ac.ir/ontology/", ""));
    return builder.toString();
  }

  private RawTriple getTriple(TokenSequenceMatcher matcher) {
    RawTriple triple = new RawTriple();
    String url = URIs.INSTANCE.getFkgResourcePrefix() + ":";
    final SequenceMatchResult.MatchedGroupInfo<CoreMap> subjectInfo = matcher.groupInfo("$subject");
    final CoreMap subjectToken = subjectInfo.nodes.iterator().next();
    if (subjectToken.containsKey(ResourceTagAnnotation.class))
      triple.setSubject(URIs.INSTANCE.replaceAllPrefixesInString(subjectToken.get(ResourceTagAnnotation.class)));
    else triple.setSubject(url + subjectInfo.text);

    final SequenceMatchResult.MatchedGroupInfo<CoreMap> objectInfo = matcher.groupInfo("$object");
//    final CoreMap objectToken = objectInfo.nodes.iterator().next();
//    if(objectToken.containsKey(ResourceTagAnnotation.class))
//      triple.setObject(URIs.INSTANCE.replaceAllPrefixesInString(objectToken.get(ResourceTagAnnotation.class)));
//    else triple.setObject(objectInfo.text);
    triple.setObject(objectInfo.text);

    final String objectEnd = matcher.group("$object2");
    if (objectEnd != null) triple.setObject(objectInfo.text + " " + objectEnd);
    //triple.setSubject(matcher.groupInfo("$subject").nodes.get(0).get(CoreAnnotations.AbbrAnnotation.class));
    // triple.setSubject(matcher.groupInfo("$object").nodes.get(0).get(CoreAnnotations.AbbrAnnotation.class));
    triple.setAccuracy(0.1);
    triple.needsMapping(true);

    return triple;
  }

  public List<RawTriple> extractTripleFromAnnotation(Annotation annotation) {
    return extractTripleFromAnnotation(annotation, null);
  }

  List<RawTriple> extractTripleFromAnnotation(Annotation annotation,
                                              List<List<ResolvedEntityToken>> preResolvedTokens) {
    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    List<RawTriple> triples = new ArrayList<>();
    List<RawTriple> sentenceTriples;
    for (int i = 0; i < sentences.size(); i++) {
      CoreMap sentence = sentences.get(i);
      int sentenceLength = sentence.get(CoreAnnotations.TextAnnotation.class).length();
      if (sentenceLength > 20 && sentenceLength < 200) {
        sentenceTriples = extractTripleFromSentence(sentence,
            preResolvedTokens != null ? preResolvedTokens.get(i) : null);
        if (sentenceTriples.size() != 0)
          triples.addAll(sentenceTriples);
      }
    }
    return triples;
  }

  public List<RawTriple> extractTripleFromText(String inputText) {
    return extractTripleFromAnnotation(new Annotation(inputText), null);
  }

}
