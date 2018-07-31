package ir.ac.iust.dml.kg.raw.distantsupervison.database;

import com.mongodb.BasicDBList;
import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import ir.ac.iust.dml.kg.raw.distantsupervison.*;
import ir.ac.iust.dml.kg.raw.extractor.EnhancedEntityExtractor;
import ir.ac.iust.dml.kg.raw.extractor.ResolvedEntityToken;
import ir.ac.iust.dml.kg.raw.extractor.ResolvedEntityTokenResource;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by hemmatan on 4/18/2017.
 */
public class CorpusDbHandler extends DbHandler {

    private MongoClient mongo = null;
    private MongoDatabase distantSupervisionDB;
    private MongoCollection<Document> corpusTable;

    public CorpusDbHandler(String tableName) {
        mongo = new MongoClient(host, port);
        distantSupervisionDB = mongo.getDatabase(Configuration.distantSupervisionDBName);
        corpusTable = distantSupervisionDB.getCollection(tableName);
    }

    public void deleteAll() {
        corpusTable.deleteMany(new Document());
    }

    public void insert(CorpusEntryObject corpusEntryObject) {

        Document document = new Document();
        document.put(Constants.sentenceAttribs.RAW, corpusEntryObject.getOriginalSentence().getRaw());
        document.put(Constants.sentenceAttribs.NORMALIZED, corpusEntryObject.getOriginalSentence().getNormalized());
        document.put(Constants.sentenceAttribs.WORDS, corpusEntryObject.getOriginalSentence().getWords());
        document.put(Constants.sentenceAttribs.POSTAG, corpusEntryObject.getOriginalSentence().getPosTagged());
        document.put(Constants.corpusDbEntryAttribs.GENERALIZED_SENTENCE, corpusEntryObject.getGeneralizedSentence());
        document.put(Constants.corpusDbEntryAttribs.SUBJECT, corpusEntryObject.getSubject());
        document.put(Constants.corpusDbEntryAttribs.OBJECT, corpusEntryObject.getObject());
        document.put(Constants.corpusDbEntryAttribs.SUBJECT_TYPE, corpusEntryObject.getSubjectType());
        document.put(Constants.corpusDbEntryAttribs.OBJECT_TYPE, corpusEntryObject.getObjectType());
        document.put(Constants.corpusDbEntryAttribs.PREDICATE, corpusEntryObject.getPredicate());
        document.put(Constants.corpusDbEntryAttribs.OCCURRENCE, corpusEntryObject.getOccurrence());
        document.put(Constants.corpusDbEntryAttribs.OBJECT_HEAD, corpusEntryObject.getObjectHead());
        document.put(Constants.corpusDbEntryAttribs.SUBJECT_HEAD, corpusEntryObject.getSubjectHead());


        corpusTable.insertOne(document);
    }

    public void insertAll(List<CorpusEntryObject> corpusEntryObjects){
        for (CorpusEntryObject corpusEntryObject:
             corpusEntryObjects) {
            this.insert(corpusEntryObject);
        }
    }

    public void loadByMostFrequentPredicates(CorpusDB destinationCorpusDB,
                                             int numberOfEntriesToLoad){
        List<String> predicates = getMostFrequentPredicates(Configuration.maximumNumberOfPredicatesToLoad);

        loadByPredicates(destinationCorpusDB, numberOfEntriesToLoad, predicates, new HashSet<>(),
                false, "", "", false, null);
    }

    public void loadByPredicates(CorpusDB destinationCorpusDB, int numberOfEntriesToLoad, List<String> predicates, Set<String> testIDs,
                                 boolean typeChecking, String allowedSubjectType, String allowedObjectType,
                                 boolean useMappings, HashMap<String, String> mapping) {
        if (!useMappings)
            for (String predicate:
                 predicates) {
                mapping.put(predicate, predicate);
            }
        List<CorpusEntryObject> corpusEntryObjects = new ArrayList<>();
        long minValue = corpusTable.count();
        for (String value:
             mapping.values()) {
            long tempCnt = corpusTable.count(new Document("predicate", value));
            if (tempCnt<=minValue)
                minValue = tempCnt;
        }

        MongoCursor cursor = corpusTable.find().iterator();
        Document document;
        String rawString;
        String normalized;
        Object wordsObject;
        BasicDBList postagObject;
        String generalizedSentence;
        String object;
        String objectHead;
        String subject;
        String subjectHead;
        List<String> objectType;
        List<String> subjectType;
        String predicate;
        int occurrence;
        List<String> words;
        List<String> posTags;
        Sentence currentSentence;
        CorpusEntryObject corpusEntryObject;
        long noOfInstacesForEachPredicate ;
        if (minValue<50)
            noOfInstacesForEachPredicate = Configuration.maximumNoOfInstancesForEachPredicate;
        else
            noOfInstacesForEachPredicate = Math.min(Configuration.maximumNoOfInstancesForEachPredicate, minValue);
        Configuration.maximumNoOfInstancesForEachPredicate = noOfInstacesForEachPredicate;
        while (cursor.hasNext() && destinationCorpusDB.getEntries().size()<numberOfEntriesToLoad){
            document = (Document) cursor.next();
            predicate = mapping.get((String) document.get(Constants.corpusDbEntryAttribs.PREDICATE));
            if ((destinationCorpusDB.getPredicateCounts().containsKey(predicate) &&
                    destinationCorpusDB.getPredicateCounts().get(predicate) >= noOfInstacesForEachPredicate)
                    || testIDs.contains(document.get("_id").toString())) {
                continue;
            }
            if (!predicates.contains(predicate))
                continue;
            rawString = (String) document.get(Constants.sentenceAttribs.RAW);
            normalized = (String) document.get(Constants.sentenceAttribs.NORMALIZED);
            words = (List<String>) document.get(Constants.sentenceAttribs.WORDS);
            posTags = (List<String>) document.get(Constants.sentenceAttribs.POSTAG);
            generalizedSentence = (String) document.get(Constants.corpusDbEntryAttribs.GENERALIZED_SENTENCE);
            object = (String) document.get(Constants.corpusDbEntryAttribs.OBJECT);
            subject = (String) document.get(Constants.corpusDbEntryAttribs.SUBJECT);
            objectHead = (String) document.get(Constants.corpusDbEntryAttribs.OBJECT_HEAD);
            subjectHead = (String) document.get(Constants.corpusDbEntryAttribs.SUBJECT_HEAD);
            objectType = (List<String>) document.get(Constants.corpusDbEntryAttribs.OBJECT_TYPE);
            subjectType = (List<String>) document.get(Constants.corpusDbEntryAttribs.SUBJECT_TYPE);
            if (subjectType.size() <= 1 || objectType.size() <= 1)
                continue;
            if (typeChecking && (!subjectType.contains(allowedSubjectType) || !objectType.contains(allowedObjectType)))
                continue;
            occurrence = (int) document.get(Constants.corpusDbEntryAttribs.OCCURRENCE);

            //words = convertBasicDBListToJavaListOfStrings(wordsObject);
            //posTags = convertBasicDBListToJavaListOfStrings(postagObject);
            currentSentence = new Sentence(rawString,words,posTags,normalized);
            corpusEntryObject = new CorpusEntryObject(currentSentence, generalizedSentence, object, subject, objectType, subjectType, predicate, occurrence,
                    subjectHead, objectHead);
            corpusEntryObjects.add(corpusEntryObject);
            destinationCorpusDB.addEntry(corpusEntryObject);
        }
    }

    public void load(CorpusDB destinationCorpusDB){
        load(destinationCorpusDB, Integer.MAX_VALUE);
    }

    public void load(CorpusDB destinationCorpusDB, int numberOfEntriesToLoad){

        MongoCursor cursor = corpusTable.find().iterator();
        int cnt = 0;
        Document document;
        String rawString;
        String normalized;
        Object wordsObject;
        BasicDBList postagObject;
        String generalizedSentence;
        String object;
        String subject;
        String objectHead;
        String subjectHead;
        List<String> objectType;
        List<String> subjectType;
        String predicate;
        int occurrence;
        List<String> words;
        List<String> posTags;
        Sentence currentSentence;
        CorpusEntryObject corpusEntryObject;
            while (cursor.hasNext() && cnt<numberOfEntriesToLoad){
                cnt+=1;
                document = (Document) cursor.next();
                rawString = (String) document.get(Constants.sentenceAttribs.RAW);
                normalized = (String) document.get(Constants.sentenceAttribs.NORMALIZED);
                words = (List<String>) document.get(Constants.sentenceAttribs.WORDS);
                posTags = (List<String>) document.get(Constants.sentenceAttribs.POSTAG);
                generalizedSentence = (String) document.get(Constants.corpusDbEntryAttribs.GENERALIZED_SENTENCE);
                objectHead = (String) document.get(Constants.corpusDbEntryAttribs.OBJECT_HEAD);
                subjectHead = (String) document.get(Constants.corpusDbEntryAttribs.SUBJECT_HEAD);
                object = (String) document.get(Constants.corpusDbEntryAttribs.OBJECT);
                subject = (String) document.get(Constants.corpusDbEntryAttribs.SUBJECT);
                objectType = (List<String>) document.get(Constants.corpusDbEntryAttribs.OBJECT_TYPE);
                subjectType = (List<String>) document.get(Constants.corpusDbEntryAttribs.SUBJECT_TYPE);
                predicate = (String) document.get(Constants.corpusDbEntryAttribs.PREDICATE);
                occurrence = (int) document.get(Constants.corpusDbEntryAttribs.OCCURRENCE);
                //words = convertBasicDBListToJavaListOfStrings(wordsObject);
                //posTags = convertBasicDBListToJavaListOfStrings(postagObject);
                currentSentence = new Sentence(rawString,words,posTags,normalized);
                corpusEntryObject = new CorpusEntryObject(currentSentence, generalizedSentence, object, subject, objectType, subjectType, predicate, occurrence,
                        subjectHead, objectHead);
                destinationCorpusDB.addEntry(corpusEntryObject);
            }

        Configuration.noOfTrainExamples = destinationCorpusDB.getEntries().size();
    }


    public List<String> getMostFrequentPredicates(int numberOfEntriesToLoad){

        Document secondGroup = new Document("$group",
                new Document("_id",
                        new Document("predicate", "$predicate"))
                        .append("count", new Document("$sum", 1)));

        Document sort = new Document("$sort", new Document("count", -1));

        List<Document> pipeline = new ArrayList<Document>(Arrays.asList(secondGroup));
        pipeline.add(sort);
        AggregateIterable<Document> documents = corpusTable.aggregate(pipeline).allowDiskUse(true);


        int cnt = 0;
        List<String> result = new ArrayList<>();
        for (Document d:
                documents) {
            if (cnt++ >= numberOfEntriesToLoad)
                break;
            String predicate = ((Document) d.get("_id")).get("predicate").toString();
            result.add(predicate);
            System.out.println(d.get("_id")+" "+d.get("count"));
        }

        return result;
    }

    public void close(){
        mongo.close();
    }


    public void loadByReadingPedicatesFromFile(CorpusDB destinationCorpusDB, int numberOfEntriesToLoad, Set<String> testIDs,
                                               String predicatesFile, String mappingsFile) {
        List<String> predicates = readPredicatesFromFile(Configuration.maximumNumberOfPredicatesToLoad, predicatesFile);
        HashMap<String, String> mappings = readMappings(mappingsFile);
        loadByPredicates(destinationCorpusDB, numberOfEntriesToLoad, predicates, testIDs,
                false, "", "", true, mappings);
    }

    private HashMap<String,String> readMappings(String mappingsFile) {
        HashMap<String, String> mappings = new HashMap<>();
        try (Scanner scanner = new Scanner(new FileInputStream(mappingsFile))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().replace("\uFEFF", "");
                String[] mapped = line.split("\t");
                for (String token:
                     mapped) {
                    mappings.put(token, mapped[0]);
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return mappings;
    }

    public static List<String> readPredicatesFromFile(int numberOfPredicatesToLoad, String predicatesFile) {
        List<String> predicates = new ArrayList<>();
        int cnt = 0;
        try (Scanner scanner = new Scanner(new FileInputStream(predicatesFile))) {
            while (scanner.hasNextLine() && cnt < numberOfPredicatesToLoad) {
                String line = scanner.nextLine().replace("\uFEFF", "");
                predicates.add(line);
                cnt++;
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return predicates;
    }

    public void updateEntityTypes() {
        final EnhancedEntityExtractor extractor = new EnhancedEntityExtractor();
        MongoCursor cursor = corpusTable.find().iterator();
        Document document;
        String rawString;
        String subject;
        String object;
        List<ResolvedEntityTokenResource> objectType;
        List<ResolvedEntityTokenResource> subjectType;
        List<List<ResolvedEntityToken>> result;
        List<List<ResolvedEntityToken>> curObjectType;
        List<List<ResolvedEntityToken>> curSubjectType;
        ResolvedEntityTokenResource entityTypeGuess;
        int cnt = 0;
        while (cursor.hasNext()) {
            System.out.println(cnt++);
            objectType = new ArrayList<>();
            subjectType = new ArrayList<>();
            document = (Document) cursor.next();
            rawString = (String) document.get(Constants.sentenceAttribs.RAW);
            if (rawString.length() > Configuration.maxLengthForRawString) {
                corpusTable.deleteOne(document);
                continue;
            }
            object = (String) document.get(Constants.corpusDbEntryAttribs.OBJECT);
            System.out.println("object: " + object);
            curObjectType = extractor.extract(object);
            if (curObjectType != null && !curObjectType.isEmpty() && curObjectType.get(0) != null && !curObjectType.get(0).isEmpty()) {
                if (curObjectType.get(0).get(0).getAmbiguities() != null &&
                        !curObjectType.get(0).get(0).getAmbiguities().isEmpty())
                    objectType.addAll(curObjectType.get(0).get(0).getAmbiguities());
                if (curObjectType.get(0).get(0).getResource() != null)
                    objectType.add(curObjectType.get(0).get(0).getResource());
            }
            subject = (String) document.get(Constants.corpusDbEntryAttribs.SUBJECT);
            System.out.println("subject: " + subject);
            curSubjectType = extractor.extract(subject);
            if (curSubjectType != null && !curSubjectType.isEmpty() && curSubjectType.get(0) != null && !curSubjectType.get(0).isEmpty()) {
                if (curSubjectType.get(0).get(0).getAmbiguities() != null &&
                        !curSubjectType.get(0).get(0).getAmbiguities().isEmpty())
                    subjectType.addAll(curSubjectType.get(0).get(0).getAmbiguities());
                if (curSubjectType.get(0).get(0).getResource() != null)
                    subjectType.add(curSubjectType.get(0).get(0).getResource());
            }
            result = extractor.extract(rawString);
            List<String> newObjectType = new ArrayList<>();
            List<String> newSubjectType = new ArrayList<>();
            extractor.disambiguateByContext(result, Configuration.contextDisambiguationThreshold);
            int i = 0;
            int j;
            boolean objSw = false;
            boolean subjSw = false;
            while (i < result.size()) {
                j = 0;
                while (j < result.get(i).size()) {
                    if (!result.get(i).get(j).getIobType().name().equalsIgnoreCase("Beginning")) {
                        j++;
                        continue;
                    }
                    entityTypeGuess = result.get(i).get(j).getResource();
                    for (ResolvedEntityTokenResource resolvedEntityTokenResource :
                            objectType) {
                        if (resolvedEntityTokenResource.equals(entityTypeGuess)) {
                            objSw = true;
                            newObjectType = new ArrayList<>(entityTypeGuess.getClasses());
                            break;
                        }
                    }
                    for (ResolvedEntityTokenResource resolvedEntityTokenResource :
                            subjectType) {
                        if (resolvedEntityTokenResource.equals(entityTypeGuess)) {
                            subjSw = true;
                            newSubjectType = new ArrayList<>(entityTypeGuess.getClasses());
                            break;
                        }
                    }
                    j++;
                }
                i++;
            }

            if (!objSw || !subjSw)
                corpusTable.deleteOne(document);

            Bson filter = new Document("_id", document.get("_id"));

            Bson newValue = new Document("object_type", newObjectType);
            Bson newValue2 = new Document("subject_type", newSubjectType);

            Bson updateOperationDocumentObj = new Document("$set", newValue);
            corpusTable.updateOne(filter, updateOperationDocumentObj);
            Bson updateOperationDocumentSubj = new Document("$set", newValue2);
            corpusTable.updateOne(filter, updateOperationDocumentSubj);

        }
    }


}
