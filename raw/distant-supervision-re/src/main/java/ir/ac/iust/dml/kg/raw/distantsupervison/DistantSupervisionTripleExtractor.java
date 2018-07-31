package ir.ac.iust.dml.kg.raw.distantsupervison;

import ir.ac.iust.dml.kg.raw.Normalizer;
import ir.ac.iust.dml.kg.raw.SentenceBranch;
import ir.ac.iust.dml.kg.raw.distantsupervison.models.Classifier;
import ir.ac.iust.dml.kg.raw.extractor.EnhancedEntityExtractor;
import ir.ac.iust.dml.kg.raw.extractor.ResolvedEntityToken;
import ir.ac.iust.dml.kg.raw.triple.RawTriple;
import ir.ac.iust.dml.kg.raw.triple.RawTripleBuilder;
import ir.ac.iust.dml.kg.raw.triple.RawTripleExtractor;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hemmatan on 9/6/2017.
 */
public class DistantSupervisionTripleExtractor implements RawTripleExtractor {

  //private final Classifier classifier;
  private HashMap<String, Classifier> classifiers = new HashMap<>();
  private EnhancedEntityExtractor enhancedEntityExtractor = new EnhancedEntityExtractor();

  public DistantSupervisionTripleExtractor() {
    initializeClassifiers();
  }


  @SuppressWarnings("Duplicates")
  @Override
  public List<RawTriple> extract(String source, String version, String text) {
    List<RawTriple> result;
    List<List<ResolvedEntityToken>> sentences = enhancedEntityExtractor.extract(
        SentenceBranch.summarize(Normalizer.removeBrackets(Normalizer.normalize(text))));
    enhancedEntityExtractor.disambiguateByContext(sentences, Configuration.contextDisambiguationThreshold);
    result = extract(source, version, sentences);
    return result;
  }

  @Override
  public List<RawTriple> extract(String source, String version, List<List<ResolvedEntityToken>> text) {
    Date date = new Date();
    List<RawTriple> result = new ArrayList<>();
    List<TripleGuess> triples = new ArrayList<>();//classifier.extractFromSingleSentenceString(text);
    final RawTripleBuilder builder = new RawTripleBuilder(Configuration.moduleName, source, date.getTime(), version);
    List<CorpusEntryObject> corpusEntries = extractCorpusEntries(text);
    Classifier classifier;
    for (CorpusEntryObject corpusEntryObject : corpusEntries) {
      String model = ModelHandler.decide(corpusEntryObject);
      classifier = classifiers.get(model);
      TripleGuess tripleGuess = classifier.extractFromCorpusEntryObject(corpusEntryObject);
      if (tripleGuess != null) triples.add(tripleGuess);
    }
    for (TripleGuess tripleGuess :
        triples) {
      RawTriple triple1 = builder.create()
          .subject(tripleGuess.getSubject()).predicate(tripleGuess.getPredicate())
          .object(tripleGuess.getObject()).rawText(tripleGuess.getOriginSentence())
          .accuracy(tripleGuess.getConfidence()).needsMapping(true);
      if (triple1.getAccuracy() > Configuration.confidenceThreshold &&
          !triple1.getPredicate().equals("negative")) {
        triple1.setAccuracy(((triple1.getAccuracy() - Configuration.confidenceThreshold)) / (1 - Configuration.confidenceThreshold));
        result.add(triple1);
      }
    }
    return result;
  }

  public List<CorpusEntryObject> extractCorpusEntries(List<List<ResolvedEntityToken>> sentences) {
    List<String> entities;
    List<List<String>> entityTypes;
    List<CorpusEntryObject> corpusEntryObjects = new ArrayList<>();

    for (List<ResolvedEntityToken> sentence : sentences) {
      if (sentence.size() > 30) continue;
      entities = new ArrayList<>();
      entityTypes = new ArrayList<>();
      StringBuilder raw = new StringBuilder();
      int s = 0;
      while (s < sentence.size()) {
        StringBuilder entity = new StringBuilder();
        raw.append(sentence.get(s).getWord()).append(" ");
        if (sentence.get(s).getIobType().name().equalsIgnoreCase("Beginning") &&
            sentence.get(s).getResource() != null && sentence.get(s).getResource().getClasses().size() != 1) {
          List<String> entityType = new ArrayList<>(sentence.get(s).getResource().getClasses());
          entity.append(sentence.get(s).getWord()).append(" ");
          s++;
          while (s < sentence.size() && sentence.get(s).getIobType().name().equalsIgnoreCase("Inside")) {
            raw.append(sentence.get(s).getWord()).append(" ");
            entity.append(sentence.get(s).getWord()).append(" ");
            s++;
          }
          entities.add(entity.toString());
          entityTypes.add(entityType);
        } else s++;
      }

      for (int i = 0; i < entities.size(); i++) {
        for (int j = i + 1; j < entities.size(); j++) {
          String subject1 = entities.get(i).trim();
          String object1 = entities.get(j).trim();
          List<String> subjectTypes1 = entityTypes.get(i);
          List<String> objectTypes1 = entityTypes.get(j);
          CorpusEntryObject corpusEntryObject1 = prepareForTest(raw.toString(), subject1, subjectTypes1, object1, objectTypes1);
          corpusEntryObjects.add(corpusEntryObject1);

          String subject2 = entities.get(j).trim();
          String object2 = entities.get(i).trim();
          List<String> subjectTypes2 = entityTypes.get(j);
          List<String> objectTypes2 = entityTypes.get(i);
          CorpusEntryObject corpusEntryObject2 = prepareForTest(raw.toString(), subject2, subjectTypes2, object2, objectTypes2);
          corpusEntryObjects.add(corpusEntryObject2);
        }
      }
    }
    return corpusEntryObjects;
  }


  private CorpusEntryObject prepareForTest(String raw, String subject, List<String> subjectTypes, String object, List<String> objectTypes) {
    CorpusEntryObject corpusEntryObject = new CorpusEntryObject();
    corpusEntryObject.setOriginalSentence(new Sentence(raw));
    String generalized = raw.replace(subject, Constants.sentenceAttribs.SUBJECT_ABV);
    generalized = generalized.replace(object, Constants.sentenceAttribs.OBJECT_ABV);
    corpusEntryObject.setGeneralizedSentence(generalized);
    corpusEntryObject.setSubject(subject);
    corpusEntryObject.setObject(object);
    corpusEntryObject.setSubjectType(subjectTypes);
    corpusEntryObject.setObjectType(objectTypes);
    corpusEntryObject.setSubjectHead(corpusEntryObject.setEntitysHead(subject));
    corpusEntryObject.setObjectHead(corpusEntryObject.setEntitysHead(object));
    return corpusEntryObject;
  }

  private void initializeClassifiers() {
    String[] names = Configuration.classifierTypes;
    for (String name :
        names) {
      File curFile = new File(SharedResources.logitDirectory + name);
      if (curFile.isDirectory()) {
        Classifier classifier = new Classifier(name);
        classifier.loadModels();
        classifiers.put(name, classifier);
      }
    }
  }
}
