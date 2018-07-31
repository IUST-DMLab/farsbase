package ir.ac.iust.nlp.jhazm;

import edu.stanford.nlp.ling.TaggedWord;
import ir.ac.iust.nlp.jhazm.utils.FileHandler;
import ir.ac.iust.nlp.jhazm.utils.StringBuilderWriter;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.maltparser.concurrent.graph.ConcurrentDependencyGraph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by Majid Asgari
 */
public class Runner {
    private static Logger logger = Logger.getLogger(Runner.class);

    public static void main(String[] args) throws IOException {
        // create the Options
        Options options = new Options();
        options.addOption("a", "action", true, "action to do. " + StringUtils.join(Action.values(), ", ") + ", are the options.");
        options.addOption("i", "input", true, "input, standard input file file.");
        options.addOption("t", "text", true, "input text");
        options.addOption("o", "output", true, "output, standard output file.");
        options.addOption("v", "verbose", true, "show output string on console. console may not support UTF-8 in some operating systems.");

        CommandLineParser parser = new BasicParser();
        Action action = null;
        Path inputPath, outputPath = null;
        String inputText = null;
        boolean verbose = true;
        try {
            CommandLine line = parser.parse(options, args);
            if (!line.hasOption("a"))
                showHelp(options);
            try {
                action = Action.valueOf(line.getOptionValue("a"));
            } catch (Exception exp) {
                logger.error("wrong action.");
                System.exit(1);
            }
          String inputFile = null;
            if (!line.hasOption("i") && !line.hasOption("t")) {
                FileHandler.prepareFile("sample.txt");
                inputFile = "sample.txt";
            }
            if (line.hasOption("i")) {
                inputFile = line.getOptionValue("i");
            }

            if (((inputFile == null && !line.hasOption("t")) || !line.hasOption("o")))
                showHelp(options);
            if (inputFile != null) {
                inputPath = Paths.get(inputFile);
                if (!Files.exists(inputPath)) {
                    logger.info("file does not exists: " + inputPath.toFile().getAbsolutePath());
                    System.exit(1);
                } else {
                    logger.info("input file: " + inputPath.toFile().getAbsolutePath());
                    byte[] encoded = Files.readAllBytes(inputPath);
                    inputText = new String(encoded, "UTF-8");
                }
            }
            if (line.hasOption("t")) inputText = line.getOptionValue("t");
            if (line.hasOption("o")) {
                outputPath = Paths.get(line.getOptionValue("o"));
                logger.info("output path is: " + outputPath.toFile().getAbsolutePath());
            }
            if (line.hasOption("v")) verbose = line.getOptionValue("v").equals("true");
        } catch (ParseException exp) {
            logger.trace(exp);
            showHelp(options);
        }

        assert action != null;
        assert outputPath != null;
        Normalizer normalizer = new Normalizer();
        inputText = normalizer.run(inputText);
        WordTokenizer tokenizer = new WordTokenizer();
        List<String> tokens = tokenizer.tokenize(inputText);
        StringBuilder builder = new StringBuilder();
        logger.info("working directory: " + System.getProperty("user.dir"));
        try {
            switch (action) {
                case Stemming:
                    logger.trace("Stemming, text = " + inputText);
                    Stemmer stemmer = new Stemmer();
                    for (String token : tokens) {
                        String stem = stemmer.stem(token);
                        if (verbose) logger.info(stem);
                        builder.append(stem).append(' ');
                    }
                    String stem = stemmer.stem(inputText);
                    if (verbose) logger.info(stem);
                    Files.write(outputPath, stem.getBytes("UTF-8"));
                    break;
                case Normalizing:
                    logger.trace("notmalizing, text = " + inputText);
                    if (verbose) logger.info(inputText);
                    Files.write(outputPath, inputText.getBytes("UTF-8"));
                    break;
                case WorkTokenizing:
                    logger.trace("tokenizing, text = " + inputText);
                    String tokenized = StringUtils.join(tokens, " ");
                    if (verbose) logger.info(tokenized);
                    assert tokenized != null;
                    Files.write(outputPath, tokenized.getBytes("UTF-8"));
                    break;
                case SentenceTokenizing:
                    logger.trace("tokenizing, text = " + inputText);
                    tokenized = StringUtils.join(tokens, " ");
                    if (verbose) logger.info(tokenized);
                    assert tokenized != null;
                    Files.write(outputPath, tokenized.getBytes("UTF-8"));
                    break;
                case Lemmatize:
                    logger.trace("lemmatize, text = " + inputText);
                    Lemmatizer lemmatizer = new Lemmatizer();
                    for (String token : tokens) {
                        String lemma = lemmatizer.lemmatize(token);
                        if (verbose) logger.info(lemma);
                        builder.append(lemma).append(' ');
                    }
                    Files.write(outputPath, builder.toString().getBytes("UTF-8"));
                    break;
                case PartOfSpeechTagging:
                    logger.trace("part of speech tagging, text = " + inputText);
                    POSTagger posTagger = new POSTagger();
                    List<TaggedWord> tagged = posTagger.batchTag(tokens);
                    if (verbose) {
                        for (TaggedWord taggedWord : tagged) {
                            logger.info(taggedWord.word() + "\t" + taggedWord.tag());
                            builder.append(taggedWord.word()).append("\t").append(taggedWord.tag()).append("\r\n");
                        }
                    }
                    Files.write(outputPath, builder.toString().getBytes("UTF-8"));
                    break;
                case DependencyParsing:
                    logger.trace("dependency parser, text = " + inputText);
                    DependencyParser dependencyParser = new DependencyParser();
                    posTagger = new POSTagger();
                    tagged = posTagger.batchTag(tokens);
                    ConcurrentDependencyGraph graph = dependencyParser.rawParse(tagged);
                    String output = graph.toString();
                    logger.info(output);
                    Files.write(outputPath, output.getBytes("UTF-8"));
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private static void showHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        final StringBuilder helpBuilder = new StringBuilder().append('\n');
        helpBuilder.append("Welcome to JHazm.").append('\n');
        formatter.printHelp(new StringBuilderWriter(helpBuilder), 80, "java -jar jhazm.jar", null,
                options, 0, 0, "Thank you", false);
        helpBuilder.append("Required options for stemmer: --i or --t, --o").append('\n');
        logger.info(helpBuilder);
        System.exit(0);
    }
}
