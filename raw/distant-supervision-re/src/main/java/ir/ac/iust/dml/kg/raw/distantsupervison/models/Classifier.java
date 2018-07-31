package ir.ac.iust.dml.kg.raw.distantsupervison.models;

import de.bwaldvogel.liblinear.*;
import ir.ac.iust.dml.kg.raw.distantsupervison.*;
import ir.ac.iust.dml.kg.raw.distantsupervison.database.CorpusDbHandler;
import ir.ac.iust.dml.kg.raw.distantsupervison.JSONHandler;
import ir.ac.iust.dml.kg.raw.extractor.ResolvedEntityToken;
import ir.ac.iust.dml.kg.resource.extractor.client.ExtractorClient;
import ir.ac.iust.dml.kg.resource.extractor.client.MatchedResource;
import ir.ac.iust.dml.kg.resource.extractor.client.ResourceType;
import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;

import java.io.*;
import java.util.*;

//import static ir.ac.iust.dml.kg.raw.distantsupervison.SharedResources.corpusDB;

/**
 * Created by hemmatan on 4/10/2017.
 */
public class Classifier {
    public CorpusDB corpusDB = new CorpusDB();

    private int defaultMaximumNoOfVocabularyForBOW = Configuration.maximumNoOfVocabularyForBagOfWords;
    private int numberOfFeatures;
    private String modelDirectoryName = Constants.classifierTypes.GENERAL;
    private String modelFilePath;
    private String predicatesIndexFile;
    public String predicatesToLoadFile;
    private String mappingsFile;

    private String goldJsonFilePath = Configuration.exportURL;
    private HashMap<String, SegmentedBagOfWords> segmentedBagOfWordsHashMap = new HashMap<>();
    private EntityTypeModel entityTypeModel;
    private PartOfSpeechModel partOfSpeechModel;
    private ObjectHeadModel objectHeadModel;
    private CorpusDB trainData = new CorpusDB();
    private Set<String> testIDs = new HashSet<>();
    private String allowedSubjectType = "http://fkg.iust.ac.ir/ontology/Thing";
    private String allowedObjectType = "http://fkg.iust.ac.ir/ontology/Thing";
    private Model model;

    public Classifier(){
        CorpusDbHandler trainDbHandler = new CorpusDbHandler(Configuration.trainTableName);
        trainDbHandler.load(trainData);
        modelFilePath = fullPath("model");
        predicatesIndexFile = fullPath("predicates.txt");
        predicatesToLoadFile = fullPath( "predicatesToLoad.txt");
        mappingsFile = fullPath("mappings.txt");
    }

    public Classifier(String modelType){
        //CorpusDbHandler trainDbHandler = new CorpusDbHandler(Configuration.trainTableName);
        //trainDbHandler.load(trainData);
        this.modelDirectoryName = modelType;
        modelFilePath = fullPath("model");
        predicatesIndexFile = fullPath("predicates.txt");
        predicatesToLoadFile = fullPath( "predicatesToLoad.txt");
        mappingsFile = fullPath("mappings.txt");
        extractAllowedEntityTypes();
    }

    private void extractAllowedEntityTypes() {
        String allowedEntityTypesFile = fullPath("allowedEntityTypes.txt");
        try {
            Scanner scanner = new Scanner(new FileInputStream(allowedEntityTypesFile));
            this.allowedSubjectType = scanner.next().replace("\uFEFF", "");
            this.allowedObjectType = scanner.next();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String fullPath(String fileName){
        return SharedResources.logitDirectory
                + this.modelDirectoryName + File.separator + fileName;
    }

    public void createTrainData(int maximumNumberOfTrainingExamples,
                                CorpusDbHandler trainDbHandler) {
        Configuration.maximumNoOfInstancesForEachPredicate = Configuration.initialMaximumNoOfInstancesForEachPredicate;
        Configuration.maximumNoOfInstancesForEachPredicate = Configuration.initialMaximumNoOfInstancesForEachPredicate;
        readTest();

        trainDbHandler.deleteAll();
        trainData.deleteAll();

        CorpusDbHandler corpusDbHandler = new CorpusDbHandler(Configuration.corpusTableName);
        if (Configuration.trainingSetMode.equalsIgnoreCase(Constants.trainingSetModes.LOAD_PREDICATES_FROM_FILE))
            corpusDbHandler.loadByReadingPedicatesFromFile(corpusDB, maximumNumberOfTrainingExamples, testIDs, this.predicatesToLoadFile,
                    this.mappingsFile);
        else if (Configuration.trainingSetMode.equalsIgnoreCase(Constants.trainingSetModes.USE_ALL_PREDICATES_IN_EXPORTS_JSON))
            corpusDbHandler.loadByReadingPedicatesFromFile(corpusDB, maximumNumberOfTrainingExamples, testIDs, SharedResources.predicatesInExportsJsonFile, this.mappingsFile);
        else // Configuration.trainingSetMode.equalsIgnoreCase(Constants.trainingSetModes.LOAD_CORPUS_FREQUENT_PREDICATES)
            corpusDbHandler.loadByMostFrequentPredicates(corpusDB, maximumNumberOfTrainingExamples);

        addSomeNegativeSamples();
        corpusDB.shuffle();
        int numberOfTrainingExamples = corpusDB.getEntries().size();
        Configuration.noOfTrainExamples = numberOfTrainingExamples;

        for (int i = 0; i < numberOfTrainingExamples; i++) {
            System.out.println(i + "\t" + corpusDB.getShuffledEntries().get(i).toString());
            trainData.addEntry(corpusDB.getShuffledEntries().get(i));
        }
        trainDbHandler.insertAll(trainData.getEntries());
    }

    private void addSomeNegativeSamples() {
        List<String> neg = new ArrayList<>();
        neg.add("negative");
        CorpusDbHandler negativeCorpusDbHandler = new CorpusDbHandler(Configuration.negativesTableName);
        negativeCorpusDbHandler.loadByPredicates(corpusDB,
                (int) (corpusDB.getEntries().size()+Configuration.maximumNoOfInstancesForEachPredicate),
               neg, new HashSet<>(), true , allowedSubjectType, allowedObjectType , false, new HashMap<>());
    }


    public void train(int maximumNumberOfTrainingExamples, boolean buildTrainDataFromScratch) {

        CorpusDbHandler trainDbHandler = new CorpusDbHandler(Configuration.trainTableName+this.modelDirectoryName);

        if (buildTrainDataFromScratch) {
            createTrainData(maximumNumberOfTrainingExamples, trainDbHandler);
            //createAndSaveTestData(problem, numberOfTestExamples);
        } else {
            trainDbHandler.load(trainData);
        }
        trainDbHandler.close();

        Problem problem = new Problem();
        problem.l = Configuration.noOfTrainExamples; // number of training examples
        FeatureNode[][] featureNodes = new FeatureNode[problem.l][];
        problem.y = new double[problem.l];// target values

        initializeModels(true);

        numberOfFeatures = 4 * this.segmentedBagOfWordsHashMap.get(Constants.segmentedBagOfWordsAttribs.OBJECT_FOLLOWING).getMaximumNoOfVocabulary() +
                +2 * this.entityTypeModel.getNoOfEntityTypes() + 2 * this.partOfSpeechModel.getNoOfPOS();// +this.objectHeadModel.getNoOfHeads();
        problem.n = this.numberOfFeatures;// number of features

        for (int i = 0; i < problem.l; i++) {
            CorpusEntryObject corpusEntryObject = trainData.getEntries().get(i);
            featureNodes[i] = FeatureExtractor.createFeatureNode(segmentedBagOfWordsHashMap, entityTypeModel, partOfSpeechModel, objectHeadModel, corpusEntryObject);
            problem.y[i] = trainData.getIndices().get(corpusEntryObject.getPredicate());
        }


        System.out.print("trainData.getIndices(): " + trainData.getIndices());
        System.out.print("numberOfFeatures: "+numberOfFeatures);

        problem.x =  featureNodes;// feature nodes

        SolverType solver = SolverType.L2R_LR; // -s 0
        double costOfConstraintsViolation = Configuration.libLinearParams.costOfConstraintsViolation;
        double eps = Configuration.libLinearParams.epsStoppingCriteria;

        Parameter parameter = new Parameter(solver, costOfConstraintsViolation, eps);

        File modelFile = new File(this.modelFilePath);


        trainData.savePredicateIndices(this.predicatesIndexFile);
        model = Linear.train(problem, parameter);

        try {
            model.save(modelFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildDepParseModels() {
        this.objectHeadModel = new ObjectHeadModel(trainData.getEntries(), 350, "objectHead");
    }

    private void loadDepParseModels() {
        this.objectHeadModel = new ObjectHeadModel(350, "objectHead");
        this.objectHeadModel.loadModel();
    }

    private void buildSegmentedBowModel() {
        int temp = (int) (this.trainData.getNumberOfClasses() * 100);
        SegmentedBagOfWords segmentedBagOfWords = new SegmentedBagOfWords(trainData.getEntries(), Constants.segmentedBagOfWordsAttribs.SUBJECT_PRECEDING,
                fullPath(Constants.segmentedBagOfWordsAttribs.SUBJECT_PRECEDING),
                false, temp / 4);
        segmentedBagOfWordsHashMap.put(Constants.segmentedBagOfWordsAttribs.SUBJECT_PRECEDING, segmentedBagOfWords);

        segmentedBagOfWords = new SegmentedBagOfWords(trainData.getEntries(), Constants.segmentedBagOfWordsAttribs.SUBJECT_FOLLOWING,
                fullPath(Constants.segmentedBagOfWordsAttribs.SUBJECT_FOLLOWING),
                false, temp / 4);
        segmentedBagOfWordsHashMap.put(Constants.segmentedBagOfWordsAttribs.SUBJECT_FOLLOWING, segmentedBagOfWords);

        segmentedBagOfWords = new SegmentedBagOfWords(trainData.getEntries(), Constants.segmentedBagOfWordsAttribs.OBJECT_PRECEDING,
                fullPath(Constants.segmentedBagOfWordsAttribs.OBJECT_PRECEDING),
                false, temp / 4);
        segmentedBagOfWordsHashMap.put(Constants.segmentedBagOfWordsAttribs.OBJECT_PRECEDING, segmentedBagOfWords);

        segmentedBagOfWords = new SegmentedBagOfWords(trainData.getEntries(), Constants.segmentedBagOfWordsAttribs.OBJECT_FOLLOWING,
                fullPath(Constants.segmentedBagOfWordsAttribs.OBJECT_FOLLOWING),
                false, temp / 4);
        segmentedBagOfWordsHashMap.put(Constants.segmentedBagOfWordsAttribs.OBJECT_FOLLOWING, segmentedBagOfWords);
    }

    private void loadSegmentedBowModel() {
        SegmentedBagOfWords segmentedBagOfWords = new SegmentedBagOfWords(Constants.segmentedBagOfWordsAttribs.SUBJECT_PRECEDING,
                fullPath(Constants.segmentedBagOfWordsAttribs.SUBJECT_PRECEDING));
        segmentedBagOfWords.loadModel();
        segmentedBagOfWordsHashMap.put(Constants.segmentedBagOfWordsAttribs.SUBJECT_PRECEDING, segmentedBagOfWords);

        segmentedBagOfWords = new SegmentedBagOfWords(Constants.segmentedBagOfWordsAttribs.SUBJECT_FOLLOWING,
                fullPath(Constants.segmentedBagOfWordsAttribs.SUBJECT_FOLLOWING));
        segmentedBagOfWords.loadModel();
        segmentedBagOfWordsHashMap.put(Constants.segmentedBagOfWordsAttribs.SUBJECT_FOLLOWING, segmentedBagOfWords);

        segmentedBagOfWords = new SegmentedBagOfWords(Constants.segmentedBagOfWordsAttribs.OBJECT_PRECEDING,
                fullPath(Constants.segmentedBagOfWordsAttribs.OBJECT_PRECEDING));
        segmentedBagOfWords.loadModel();
        segmentedBagOfWordsHashMap.put(Constants.segmentedBagOfWordsAttribs.OBJECT_PRECEDING, segmentedBagOfWords);

        segmentedBagOfWords = new SegmentedBagOfWords(Constants.segmentedBagOfWordsAttribs.OBJECT_FOLLOWING,
                fullPath(Constants.segmentedBagOfWordsAttribs.OBJECT_FOLLOWING));
        segmentedBagOfWords.loadModel();
        segmentedBagOfWordsHashMap.put(Constants.segmentedBagOfWordsAttribs.OBJECT_FOLLOWING, segmentedBagOfWords);
    }

    public void loadModels() {
        model = null;
        File modelFile = new File(this.modelFilePath);
        try {
            model = Linear.loadModel(modelFile);

        } catch (IOException e) {
            e.printStackTrace();
        }



        entityTypeModel = new EntityTypeModel(true, fullPath("entity.txt"));
        partOfSpeechModel = new PartOfSpeechModel(fullPath( "posModel.txt"));
        partOfSpeechModel.loadModel();
        loadSegmentedBowModel();
        //loadDepParseModels();
        //TODO: temp
        this.objectHeadModel = new ObjectHeadModel(350, "objectHead");

        trainData.loadPredicateIndices(this.predicatesIndexFile);
    }

    public void initializeModels(boolean train) {
        entityTypeModel = new EntityTypeModel(false, fullPath("entity.txt"));
        partOfSpeechModel = new PartOfSpeechModel(fullPath( "posModel.txt"));
        for (int i = 0; i < trainData.getEntries().size(); i++) {
            CorpusEntryObject corpusEntryObject = trainData.getEntries().get(i);
            partOfSpeechModel.addToModel(corpusEntryObject.getOriginalSentence().getPosTagged());
        }
        partOfSpeechModel.saveModel();
        //buildDepParseModels();
        buildSegmentedBowModel();
    }


    public boolean ignoreEntity(MatchedResource matchedResource, Sentence test) {
        boolean sw = false;
        //TaggedWord pos = POSTagger.tag(Arrays.asList(test.getRaw().split(" ")[matchedResource.getStart()])).get(0);


        sw = (matchedResource.getResource() == null ||
                matchedResource.getResource().getClassTree() == null ||
                matchedResource.getResource().getClassTree().size() == 0 ||
                (matchedResource.getResource().getType() != null &&
                        matchedResource.getResource().getType() == ResourceType.Property) /*||
                (matchedResource.getEnd() == matchedResource.getStart() &&
                        pos.tag().equalsIgnoreCase("P"))*/
        );


        return sw;
    }

    public void testForSingleSentenceStringAndTriple(String sentenceString, String subject, String object, String predicate) {
        Sentence test = new Sentence(sentenceString);
        ExtractorClient client = new ExtractorClient(Configuration.extractorClient);
        List<MatchedResource> resultsForSubject = client.match(subject);//TODO
        List<MatchedResource> resultsForObject = client.match(object);


        //Feature[] instance = bagOfWordsModel.createBowLibLinearFeatureNodeForQueryWithWindow(test.getWords());
        Model model = null;
        File modelFile = new File(String.valueOf(this.getClass().getClassLoader().getResourceAsStream(this.modelFilePath)));
        try {
            model = Linear.loadModel(modelFile);

        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> subjectType = new ArrayList<>();
        if (resultsForSubject != null && !resultsForSubject.isEmpty()
                && resultsForSubject.get(0).getResource() != null)
            subjectType.addAll(resultsForSubject.get(0).getResource().getClassTree());
        List<String> objectType = new ArrayList<>();
        if (resultsForObject != null && !resultsForObject.isEmpty()
                && resultsForObject.get(0).getResource() != null)
            objectType.addAll(resultsForObject.get(0).getResource().getClassTree());

        String generalized = sentenceString.replace(subject, Constants.sentenceAttribs.SUBJECT_ABV);
        generalized = generalized.replace(object, Constants.sentenceAttribs.OBJECT_ABV);

        CorpusEntryObject corpusEntryObject = new CorpusEntryObject();
        corpusEntryObject.setOriginalSentence(test);
        corpusEntryObject.setObject(object);
        corpusEntryObject.setSubject(subject);
        corpusEntryObject.setGeneralizedSentence(generalized);
        corpusEntryObject.setSubjectType(subjectType);
        corpusEntryObject.setObjectType(objectType);

        corpusEntryObject.setSubjectHead(corpusEntryObject.setEntitysHead(subject));
        corpusEntryObject.setObjectHead(corpusEntryObject.setEntitysHead(object));

        Feature[] instance = FeatureExtractor.createFeatureNode(segmentedBagOfWordsHashMap, entityTypeModel, partOfSpeechModel, objectHeadModel, corpusEntryObject);

        double[] probs = new double[model.getNrClass()];
        double prediction = Linear.predictProbability(model, instance, probs);
        List a = Arrays.asList(ArrayUtils.toObject(probs));

        if ((double) Collections.max(a) > 0) {
            System.out.println(subjectType);
            System.out.println(objectType);
            System.out.println("\n" + "Subject: " + subject + " " + "\n" + "Object: " + object + " " + "\n" + "Predicate: " + trainData.getInvertedIndices().get(prediction));
            System.out.println("Correct Predicate: " + predicate);
            System.out.println("Prediction number: " + prediction);
            System.out.println("Confidence: " + Collections.max(a));
        }
    }

    public List<TripleGuess> extractFromSingleSentenceString(List<List<ResolvedEntityToken>> sentences) {
        List<TripleGuess> guessList = new ArrayList<>();
        List<String> subjects;
        List<String> objects;
        List<List<String>> subjectTypes;
        List<List<String>> objectTypes;


        Model model = null;
        File modelFile = new File(this.getClass().getClassLoader().getResource("model").getFile());
        try {
            model = Linear.loadModel(modelFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
        //this.loadModels();

        for (List<ResolvedEntityToken> sentence : sentences) {
            if (sentence.size() > 30) continue;
            subjects = new ArrayList<>();
            objects = new ArrayList<>();
            subjectTypes = new ArrayList<>();
            objectTypes = new ArrayList<>();
            String raw = "";
            int s = 0;
            int o;
            while (s < sentence.size()) {
                String subject = "";
                String object = "";
                List<String> subjectType = new ArrayList<>();
                List<String> objectType = new ArrayList<>();
                raw += sentence.get(s).getWord() + " ";
                if (sentence.get(s).getIobType().name().equalsIgnoreCase("Beginning") &&
                        sentence.get(s).getResource() != null & sentence.get(s).getResource().getClasses().size() != 1) {
                    subjectType.addAll(sentence.get(s).getResource().getClasses());
                    subject += sentence.get(s).getWord() + " ";
                    s++;
                    while (s < sentence.size() && sentence.get(s).getIobType().name().equalsIgnoreCase("Inside")) {
                        raw += sentence.get(s).getWord() + " ";
                        subject += sentence.get(s).getWord() + " ";
                        s++;
                    }
                    o = s;
                    while (o < sentence.size()) {
                        object = "";
                        if (sentence.get(o).getIobType().name().equalsIgnoreCase("Beginning") &&
                                sentence.get(o).getResource() != null && sentence.get(o).getResource().getClasses().size() != 1) {
                            objectType.addAll(sentence.get(o).getResource().getClasses());
                            object += sentence.get(o).getWord() + " ";
                            o++;
                            while (o < sentence.size() && sentence.get(o).getIobType().name().equalsIgnoreCase("Inside")) {
                                object += sentence.get(o).getWord() + " ";
                                o++;
                            }
                            subjects.add(subject);
                            objects.add(object);
                            subjectTypes.add(subjectType);
                            objectTypes.add(objectType);
                        } else o++;
                    }
                } else s++;
            }
            for (int i = 0; i < subjects.size(); i++) {
                String subject = subjects.get(i).trim();
                String object = objects.get(i).trim();
                CorpusEntryObject corpusEntryObject = new CorpusEntryObject();
                corpusEntryObject.setOriginalSentence(new Sentence(raw));
                String generalized = raw.replace(subject, Constants.sentenceAttribs.SUBJECT_ABV);
                generalized = generalized.replace(object, Constants.sentenceAttribs.OBJECT_ABV);
                corpusEntryObject.setGeneralizedSentence(generalized);
                corpusEntryObject.setSubject(subject);
                corpusEntryObject.setObject(object);
                corpusEntryObject.setSubjectType(subjectTypes.get(i));
                corpusEntryObject.setObjectType(objectTypes.get(i));
                corpusEntryObject.setSubjectHead(corpusEntryObject.setEntitysHead(subject));
                corpusEntryObject.setObjectHead(corpusEntryObject.setEntitysHead(object));
                Feature[] instance = FeatureExtractor.createFeatureNode(segmentedBagOfWordsHashMap, entityTypeModel, partOfSpeechModel, objectHeadModel, corpusEntryObject);

                double[] probs = new double[model.getNrClass()];
                double prediction = Linear.predictProbability(model, instance, probs);
                List a = Arrays.asList(ArrayUtils.toObject(probs));
                double confidence = (double) Collections.max(a);
                String predicate = trainData.getInvertedIndices().get(prediction);

                TripleGuess tripleGuess = new TripleGuess(subject, object, predicate, confidence, raw);
                tripleGuess.setSource("wiki");
                if (subjectTypes.get(i).contains(this.allowedSubjectType) &&
                        objectTypes.get(i).contains(this.allowedObjectType))
                    guessList.add(tripleGuess);
            }
        }
        return guessList;
    }


    public void testOnGoldJson() {
        JSONArray jsonArray = JSONHandler.getJsonArrayFromURL(goldJsonFilePath);
        for (int i = 0; i < jsonArray.length(); i++) {
            String sentenceString = jsonArray.getJSONObject(i).getString("raw");
            String subject = jsonArray.getJSONObject(i).getString("subject");
            String object = jsonArray.getJSONObject(i).getString("object");
            String predicate = jsonArray.getJSONObject(i).getString("predicate");
            testForSingleSentenceStringAndTriple(sentenceString, subject, object, predicate);
        }
    }

    public void readTest() {
        Set<String> predicates = new HashSet<>();
        JSONArray jsonArray = JSONHandler.getJsonArrayFromURL(goldJsonFilePath);
        Configuration.noOfTestExamples = jsonArray.length();
        for (int i = 0; i < Configuration.noOfTestExamples; i++) {
            String id = jsonArray.getJSONObject(i).getString("id");
            String predicate = jsonArray.getJSONObject(i).getString("predicate");
            if (Configuration.omitGoldDataFromTrainData) testIDs.add(id);
            predicates.add(predicate);
        }

        try (Writer fileWriter = new FileWriter(SharedResources.predicatesInExportsJsonFile)) {
            for (String predicate : predicates) {
                fileWriter.write(predicate + "\r\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TripleGuess extractFromCorpusEntryObject(CorpusEntryObject corpusEntryObject) {
        if (!corpusEntryObject.getSubjectType().contains(this.allowedSubjectType) ||
                !corpusEntryObject.getObjectType().contains(this.allowedObjectType))
            return null;
        Feature[] instance = FeatureExtractor.createFeatureNode(segmentedBagOfWordsHashMap, entityTypeModel, partOfSpeechModel, objectHeadModel, corpusEntryObject);
        double[] probs = new double[model.getNrClass()];
        double prediction = Linear.predictProbability(model, instance, probs);
        List a = Arrays.asList(ArrayUtils.toObject(probs));
        double confidence = (double) Collections.max(a);
        String predicate = trainData.getInvertedIndices().get(prediction);

        TripleGuess tripleGuess = new TripleGuess(corpusEntryObject.getSubject(), corpusEntryObject.getObject(),
                predicate, confidence, corpusEntryObject.getOriginalSentence().getRaw());
        tripleGuess.setSource("wiki");
        return tripleGuess;
    }
}
