package ir.ac.iust.dml.kg.raw.distantsupervison;

import ir.ac.iust.dml.kg.raw.utils.ConfigReader;
import ir.ac.iust.dml.kg.raw.utils.dump.triple.TripleData;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemmatan on 4/5/2017.
 */
public class SharedResources {
    public static final Path tuplesPath = ConfigReader.INSTANCE.getPath("tuples.folder", "~/.pkg/data/tuples");
    public static final Path rawTextPath = ConfigReader.INSTANCE.getPath("raw.text.file", "~/.pkg/data/raw.txt");
    public static final Path corpusPath = ConfigReader.INSTANCE.getPath("corpus.file", "~/.pkg/data/corpus.txt");
    public static final Path bagOfWordsModelPath = ConfigReader.INSTANCE.getPath("bagOfWords.model", "~/.pkg/data/bagOfWords");
    public static final Path logitPath = ConfigReader.INSTANCE.getPath("logit.folder", "~/.pkg/data/logit");


    public static String rawText = "";
    public static List<String> rawTextLines = new ArrayList<String>();

    public static CorpusDB negativeSamlpesDB = new CorpusDB();

    public static final String logitDirectory = logitPath + File.separator;
    public static final String predicatesToLoadFile_shared = logitDirectory+"predicatesToLoad.txt";
    public static final String predicatesInExportsJsonFile = logitDirectory+"predicatesInExportsJson.txt";
    public static final String mappingsFile = logitDirectory+"mappings.txt";
    public static final String LastTestResultsFile = "testResults.txt";

    public static Corpus corpus = new Corpus();

}
