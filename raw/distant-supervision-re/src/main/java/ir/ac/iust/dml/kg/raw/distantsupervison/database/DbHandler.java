package ir.ac.iust.dml.kg.raw.distantsupervison.database;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.mongodb.BasicDBList;
import ir.ac.iust.dml.kg.raw.Normalizer;
import ir.ac.iust.dml.kg.raw.distantsupervison.Configuration;
import ir.ac.iust.dml.kg.raw.distantsupervison.Constants;
import ir.ac.iust.dml.kg.raw.distantsupervison.CorpusEntryObject;
import ir.ac.iust.dml.kg.raw.distantsupervison.Sentence;
import ir.ac.iust.dml.kg.raw.extractor.EnhancedEntityExtractor;
import ir.ac.iust.dml.kg.raw.extractor.ResolvedEntityToken;
import ir.ac.iust.dml.kg.resource.extractor.client.ExtractorClient;
import ir.ac.iust.dml.kg.resource.extractor.client.MatchedResource;
import org.junit.Test;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemmatan on 4/9/2017.
 */
public class DbHandler {
    protected static final String host = "localhost";
    protected static final int port = 27017;


    public List<String> convertBasicDBListToJavaListOfStrings(BasicDBList basicDBList){
        List<String> result = new ArrayList<>();
        for (Object obj:
             basicDBList) {
            result.add((String) obj);
        }
        return result;
    }

    public static void saveCorpusJasonToDB(String tempCorpusJasonPath) {

        //String tempCorpusJasonPath = "C:\\Users\\hemmatan\\IdeaProjects\\RE\\NegativeSentence.json";
        try (JsonReader reader = new JsonReader(new FileReader(tempCorpusJasonPath));
        ) {
            reader.beginArray();
            JsonToken nextToken = reader.peek();

            CorpusDbHandler corpusDbHandler = new CorpusDbHandler(Configuration.corpusTableName);
            ExtractorClient client = new ExtractorClient(Configuration.extractorClient);

            while (reader.hasNext()) {
                if (JsonToken.BEGIN_OBJECT.equals(nextToken)) {
                    reader.beginObject();
                    String name = reader.nextName();
                    String subject = Normalizer.normalize(reader.nextString());
                    name = reader.nextName();
                    String object = Normalizer.normalize(reader.nextString());
                    name = reader.nextName();
                    String predicate = reader.nextString();
                    name = reader.nextName();
                    reader.beginObject();
                    JsonToken nextToken2 = reader.peek();
                    while (!JsonToken.END_OBJECT.equals(nextToken2)) {
                        String newSentence = reader.nextName();
                        int occur = Integer.valueOf(reader.nextString());

                        String originalSentence = newSentence.replace(Constants.sentenceAttribs.SUBJECT_ABV, subject);
                        originalSentence = originalSentence.replace(Constants.sentenceAttribs.OBJECT_ABV, object);


                        List<String> objectType = new ArrayList<>();
                        List<String> subjectType = new ArrayList<>();

                        final List<MatchedResource> result_object = client.match(object);
                        final List<MatchedResource> result_subject = client.match(subject);


                        EnhancedEntityExtractor e = new EnhancedEntityExtractor();
                        List<List<ResolvedEntityToken>> result = e.extract(originalSentence);
                        e.disambiguateByContext(result, Configuration.contextDisambiguationThreshold);


                        CorpusEntryObject.setEntityType(result_object, objectType);
                        CorpusEntryObject.setEntityType(result_subject, subjectType);

                        Sentence sentence = new Sentence(originalSentence);
                        //String generalizedNormalizedSentence = sentence.getNormalized().replace(subject, Constants.sentenceAttribs.SUBJECT_ABV);
                        //generalizedNormalizedSentence = generalizedNormalizedSentence.replace(object, Constants.sentenceAttribs.OBJECT_ABV);
                        String generalizedNormalizedSentence = Normalizer.normalize(newSentence);
                        CorpusEntryObject corpusEntryObject = new CorpusEntryObject(sentence, generalizedNormalizedSentence, object, subject, objectType, subjectType, predicate, occur);
                        corpusDbHandler.insert(corpusEntryObject);
                        nextToken2 = reader.peek();
                    }
                    reader.endObject();
                    reader.endObject();
                    nextToken = reader.peek();
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void addNegativesToDB() {
        // TODO
    }



}
