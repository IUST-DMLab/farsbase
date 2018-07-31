package ir.ac.iust.dml.kg.raw.triple;

import ir.ac.iust.dml.kg.raw.rulebased.RuleBasedTripleExtractor;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RawTripleApplication.class)
public class TestExtractTriple_RuleBased {
    @Autowired
    private RuleBasedTripleExtractor extractor;

    @Test
    public void testExtractTripleRuleBased() throws IOException {

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sample_sentences.txt");
        StringWriter writer = new StringWriter();
        IOUtils.copy(inputStream, writer, Charset.forName("UTF-8"));
        String input = writer.toString();

        List<RawTriple> tripleList = extractor.extract(null, null, input);

        System.out.println(tripleList.toString());
    }
}