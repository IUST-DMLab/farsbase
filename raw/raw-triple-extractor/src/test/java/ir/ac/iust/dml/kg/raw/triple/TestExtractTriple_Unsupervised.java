/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2018)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.triple;

import ir.ac.iust.dml.kg.raw.services.unsupervised.UnsupervisedTripleExtractor;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.List;

public class TestExtractTriple_Unsupervised {
  private UnsupervisedTripleExtractor extractor = new UnsupervisedTripleExtractor();

  @Test
  public void testExtractTripleUnsupervised() throws IOException {

    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sample_sentences.txt");
    StringWriter writer = new StringWriter();
    IOUtils.copy(inputStream, writer, Charset.forName("UTF-8"));
    String input = writer.toString();

    List<RawTriple> tripleList = extractor.extract(null, null, input);

    for (RawTriple triple : tripleList) System.out.println(triple.toString());
  }
}
