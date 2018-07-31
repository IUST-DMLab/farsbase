package ir.ac.iust.dml.kg.raw.rulebased;

import edu.stanford.nlp.pipeline.Annotation;
import ir.ac.iust.dml.kg.raw.TextProcess;
import ir.ac.iust.dml.kg.raw.triple.RawTriple;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mohammad Abdous md.abdous@gmail.com
 * @version 1.1.0
 * @since 5/4/17 12:14 PM
 */
public class ExtractTripleTest {

    @Test
    public void testInNews() throws IOException {
        new Main().testInNews();
    }


    @Test
    public void testExtractTriple() throws IOException {


        String inputPath = "inputText.txt";
        String rulesPath = "tripleRules.txt";

        if (Files.notExists(Paths.get(inputPath)))
            Files.copy(ExtractTriple.class.getResourceAsStream("/inputText.txt"), Paths.get(inputPath));
        if (Files.notExists(Paths.get(rulesPath)))
            Files.copy(ExtractTriple.class.getResourceAsStream("/tripleRules.txt"), Paths.get(rulesPath));

        List<String> lines = Files.readAllLines(Paths.get(inputPath), Charset.forName("UTF-8"));
        for (String line : lines) {
            System.out.println("سلام: " + line);
        }
        // lines.remove(0);
        List<RawTriple> tripleList = new ArrayList<RawTriple>();
        TextProcess tp = new TextProcess();
        ExtractTriple extractTriple = RuleFileLoader.load(rulesPath);
        assert extractTriple != null;

        for (String line : lines) {
            Annotation annotation = new Annotation(line);
            tp.preProcess(annotation);

            tripleList.addAll(extractTriple.extractTripleFromAnnotation(annotation));
        }

        System.out.println(tripleList.toString());
    }
}
