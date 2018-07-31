/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.services.tree;

import com.google.common.collect.Lists;
import edu.stanford.nlp.ling.TaggedWord;
import ir.ac.iust.dml.kg.raw.Normalizer;
import ir.ac.iust.dml.kg.raw.POSTagger;
import ir.ac.iust.dml.kg.raw.SentenceTokenizer;
import ir.ac.iust.dml.kg.raw.WordTokenizer;
import ir.ac.iust.dml.kg.raw.services.access.entities.Occurrence;
import ir.ac.iust.dml.kg.raw.services.access.repositories.OccurrenceRepository;
import ir.ac.iust.dml.kg.raw.utils.ConfigReader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class NewsLogic {

  @Autowired
  private OccurrenceRepository dao;
  private final Log logger = LogFactory.getLog(getClass());

  public void writeToDatabase() throws IOException {
    final Path path = ConfigReader.INSTANCE.getPath("news.raw", "~/news1.csv");
    if (Files.notExists(path)) {
      logger.fatal("address " + path.toAbsolutePath().toString() + " is not existed.");
      return;
    }
    List<String>  lines= FileUtils.readLines(path.toFile());
    Long sentenceIndex = 0L;
/*
        List<LanguageProfile> languageProfiles = new LanguageProfileReader().readAllBuiltIn();

//build language detector:
        LanguageDetector languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
                .withProfiles(languageProfiles)
                .build();

//create a text object factory
        TextObjectFactory textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();

//query:
        TextObject textObject = textObjectFactory.forText("my text");
        Optional<LdLocale> lang = languageDetector.detect(textObject);
*/

    for (String line : lines) {
      List<String> sentences = SentenceTokenizer.SentenceSplitterRaw(line);
      for (String sentence : sentences) {
        if(sentence.length()>20)
          if(!sentence.contains("~") && !sentence.contains("َ")&& !sentence.contains("ُ")) {
            Occurrence occurrence = dao.getByNormalized(sentence);
            if (occurrence == null) {
              occurrence = new Occurrence();
              occurrence.setOccurrence(1);
              occurrence.setRaw(sentence);
              occurrence.setNormalized(Normalizer.normalize(sentence));
              final List<String> words = WordTokenizer.tokenize(sentence);
              occurrence.setWords(words);
              occurrence.setPosTags(Lists.transform(POSTagger.tag(words), TaggedWord::tag));
            } else occurrence.setOccurrence(occurrence.getOccurrence() + 1);
            dao.save(occurrence);
          }

        sentenceIndex++;
        if (sentenceIndex % 1000 == 0) logger.info(sentenceIndex);
      }

    }

  }
}
