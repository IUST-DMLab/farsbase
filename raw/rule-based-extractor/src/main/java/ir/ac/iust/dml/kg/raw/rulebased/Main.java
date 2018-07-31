/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Mohammad Abdous.
 */

package ir.ac.iust.dml.kg.raw.rulebased;

import edu.stanford.nlp.pipeline.Annotation;
import ir.ac.iust.dml.kg.raw.TextProcess;
import ir.ac.iust.dml.kg.raw.triple.RawTriple;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
  public static void main(String[] args) throws IOException {

    String inputPath = "input.txt";
    String outputPath = "outputTxt.txt";
    String rulesPath = "tripleRules.txt";

    if (args.length > 0) inputPath = args[0];
    if (args.length > 1) outputPath = args[1];
    if (args.length > 2) rulesPath = args[2];

    if (Files.notExists(Paths.get(inputPath)))
      Files.copy(ExtractTriple.class.getResourceAsStream("/inputText.txt"), Paths.get(inputPath));
    if (Files.notExists(Paths.get(rulesPath)))
      Files.copy(ExtractTriple.class.getResourceAsStream("/tripleRules.txt"), Paths.get(rulesPath));

    List<String> lines = null;
    try {
      lines = FileUtils.readLines(new File(inputPath), "UTF-8");
    } catch (IOException e) {
      e.printStackTrace();
    }

    List<RawTriple> tripleList = new ArrayList<RawTriple>();
    TextProcess tp = new TextProcess();
    ExtractTriple extractTriple = RuleFileLoader.load(rulesPath);
    assert extractTriple != null;
    assert lines != null;
    for (String line : lines) {
      Annotation annotation = new Annotation(line);
      tp.preProcess(annotation);
      tripleList.addAll(extractTriple.extractTripleFromAnnotation(annotation));
    }

    TripleJsonProducer.write(tripleList, Paths.get(outputPath));
  }

  public static void testInNews() throws IOException {

    String inputPath = "D:\\extract triple1\\newsCorpus\\news1.csv";
    String outputPath = "outputTxt.txt";
    String rulesPath = "tripleRules.txt";


    if (Files.notExists(Paths.get(inputPath)))
      Files.copy(ExtractTriple.class.getResourceAsStream("/inputText.txt"), Paths.get(inputPath));
    if (Files.notExists(Paths.get(rulesPath)))
      Files.copy(ExtractTriple.class.getResourceAsStream("/tripleRules.txt"), Paths.get(rulesPath));

    List<String> lines = null;
    try {
      lines = FileUtils.readLines(new File(inputPath), "UTF-8");
    } catch (IOException e) {
      e.printStackTrace();
    }

    List<RawTriple> tripleList = new ArrayList<RawTriple>();
    TextProcess tp = new TextProcess();
    ExtractTriple extractTriple = RuleFileLoader.load(rulesPath);
    assert extractTriple != null;
    assert lines != null;
    double index=0;
    for (String line : lines) {
      System.out.println(line);
      Annotation annotation = new Annotation(line);
      tp.preProcess(annotation);
      tripleList.addAll(extractTriple.extractTripleFromAnnotation(annotation));
      System.out.println(index);
      index++;
    }

    TripleJsonProducer.write(tripleList, Paths.get(outputPath));
  }
}
