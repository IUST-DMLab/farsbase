package ir.ac.iust.dml.kg.raw.distantsupervison;

import ir.ac.iust.dml.kg.raw.POSTagger;
import ir.ac.iust.dml.kg.raw.WordTokenizer;
import ir.ac.iust.dml.kg.raw.distantsupervison.database.CorpusDbHandler;
import ir.ac.iust.dml.kg.raw.distantsupervison.database.ExtractedTriplesDBHandler;
import ir.ac.iust.dml.kg.raw.distantsupervison.database.SentenceDbHandler;
import ir.ac.iust.dml.kg.raw.distantsupervison.models.Classifier;
import ir.ac.iust.dml.kg.raw.distantsupervison.models.ModelCopier;
import ir.ac.iust.dml.kg.raw.triple.RawTriple;

import java.io.*;
import java.util.*;

/**
 * Created by hemmatan on 4/4/2017.
 */
public class Test {

    @org.junit.Test
    public void postest() {
        System.out.print(POSTagger.tag(WordTokenizer.tokenize("۱۳۳۰")));
    }


    @org.junit.Test
    public void trainAll() {
        ModelHandler.trainAllModels();
    }

    @org.junit.Test
    public void train() {
        Date date = new Date();
        String dateString = date.toString().replaceAll("[: ]", "-");

        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream("consoleOutput-" + dateString + ".txt"));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.setOut(out);

        SentenceDbHandler sentenceDbHandler = new SentenceDbHandler();
        sentenceDbHandler.loadSentenceTable();

        Classifier classifier = new Classifier();

        classifier.train((int) Configuration.maximumNumberOfTrainExamples, true);
    }


    @org.junit.Test
    public void evaluateTest() {
        evaluate(SharedResources.LastTestResultsFile);
    }

    public void evaluate(String testResultsFile) {

        String predicatesFile = SharedResources.predicatesToLoadFile_shared;
        if (Configuration.trainingSetMode.equalsIgnoreCase(Constants.trainingSetModes.LOAD_PREDICATES_FROM_FILE))
            predicatesFile = SharedResources.predicatesToLoadFile_shared;
        else if (Configuration.trainingSetMode.equalsIgnoreCase(Constants.trainingSetModes.USE_ALL_PREDICATES_IN_EXPORTS_JSON))
            predicatesFile = SharedResources.predicatesInExportsJsonFile;

        List<String> predicatesToLoad = CorpusDbHandler.readPredicatesFromFile(Configuration.maximumNumberOfPredicatesToLoad, predicatesFile);
        HashMap<String, Integer> mapping = new HashMap<>();
        int currentIndex = 0;
        try (Scanner scanner = new Scanner(new FileInputStream(SharedResources.mappingsFile))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().replace("\uFEFF", "");
                String[] tokens = line.split("\t");
                for (int i = 0; i < tokens.length; i++) {
                    mapping.put(tokens[i], currentIndex);
                }
                currentIndex++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int total = 0;
        int correct = 0;
        int rec = 0;

        try (Scanner scanner = new Scanner(new FileInputStream(testResultsFile))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().replace("\uFEFF", "");
                if (line.startsWith("[main]"))
                    rec++;
                if (line.startsWith("Predicate")) {
                    String predicate = line.split(": ")[1];
                    String nextLine = scanner.nextLine();
                    String correctPredicate = nextLine.split(": ")[1];
                    String templine = scanner.nextLine();
                    String confidence = scanner.nextLine().split(": ")[1];
                    Double conf = Double.parseDouble(confidence);
                    if (conf > 0.4 && predicatesToLoad.contains(correctPredicate)) {
                        total++;
                        if (Objects.equals(mapping.get(predicate), mapping.get(correctPredicate)))
                            correct++;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        rec /= 4;
        System.out.println((correct * 100) / total);
        System.out.println(correct + " " + rec);

        System.out.println((correct * 100) / rec);

    }


    public static void main(String[] args) {
        //RawTextHandler.loadRawText();
        //RawTextHandler.buildCorpus();
        //RawTextHandler.saveCorpus();
        //RawTextHandler.loadCorpus();


        /*final Path directory = ConfigReader.INSTANCE.getPath("tuples.folder", "~/.pkg/data/tuples");

        LanguageChecker.INSTANCE.isEnglish("Test");
        final List<Path> files = PathWalker.INSTANCE.getPath(directory, new Regex("\\d-infoboxes.json"));
        final TripleJsonFileReader reader = new TripleJsonFileReader(files.get(0));
        while(reader.hasNext()) {
            final TripleData triple = reader.next();
            tripleDataList.add(triple);
        }*/


        // SentenceDbHandler sentenceDbHandler = new SentenceDbHandler();
        ////sentenceDbHandler.createCorpusTableFromWikiDump();
        //sentenceDbHandler.loadSentenceTable();
        //BagOfWordsModel bagOfWordsModel = new BagOfWordsModel(corpus.getSentences(), false, 10000);
    }

    @org.junit.Test
    public void extract() {
        /*Classifier classifier = new Classifier();
        classifier.loadModels();*/
        ModelCopier.prepare();
        String text;
        DistantSupervisionTripleExtractor distantSupervisionTripleExtractor = new DistantSupervisionTripleExtractor();
        //String text = "زاگرس در ایران واقعا است";
        ExtractedTriplesDBHandler extractedTriplesDBHandler = new ExtractedTriplesDBHandler("extracted_triples");
        try (Scanner scanner = new Scanner(Test.class.getResourceAsStream("/extract_test.txt"), "UTF-8")) {
            while (scanner.hasNextLine()) {
                text = scanner.nextLine().replace("\uFEFF", "");
                //text = "زاگرس در ایران واقعا است";
                List<RawTriple> triples = distantSupervisionTripleExtractor.extract("wiki", "2", text);
                int tem = 0;
                for (RawTriple tripleGuess :
                        triples) {
                    if (tripleGuess.getAccuracy() > Configuration.confidenceThreshold
                            &&
                            !tripleGuess.getPredicate().equalsIgnoreCase("negative"))
                        extractedTriplesDBHandler.insert(tripleGuess);
                }
            }
        }
    }


    @org.junit.Test
    public void update() {
        CorpusDbHandler corpus = new CorpusDbHandler(Configuration.corpusTableName);
        corpus.updateEntityTypes();
    }

    @org.junit.Test
    public void trainByType() {
        Date date = new Date();
        String dateString = date.toString().replaceAll("[: ]", "-");

        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream("consoleOutput-" + dateString + ".txt"));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.setOut(out);

        Classifier classifier = new Classifier(Constants.classifierTypes.WORK_AGENT);
        classifier.train((int) Configuration.maximumNumberOfTrainExamples, true);
    }


}
