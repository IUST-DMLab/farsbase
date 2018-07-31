/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.virtuoso;

import ir.ac.iust.dml.kg.virtuoso.connector.VirtuosoConnector;
import ir.ac.iust.dml.kg.virtuoso.connector.data.VirtuosoTriple;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class Tester {
  @Test
  public void test() throws UnsupportedEncodingException {
    final String GRAPH_NAME = "http://fkg.iust.ac.ir/test/";
    final String resourcePrefix = GRAPH_NAME + "resource/";
    final String ontologyPrefix = GRAPH_NAME + "ontology/";
    final VirtuosoConnector connector = new VirtuosoConnector(GRAPH_NAME);
    assert connector.clear();
    String SUBJECT = resourcePrefix + "ب";
    assert connector.addResource(SUBJECT, ontologyPrefix + "p1", resourcePrefix + "o1");
    assert connector.addLiteral(SUBJECT, ontologyPrefix + "p2", "string");
    assert connector.addLiteral(SUBJECT, ontologyPrefix + "p1", 0.2);

    List<VirtuosoTriple> triples = connector.getTriplesOfSubject(SUBJECT);
    assert !triples.isEmpty();
    for (VirtuosoTriple t : triples) {
      final String value = t.getObject().getValue().toString();
      switch (t.getObject().getType()) {
        case Double:
          assert value.equals("0.2");
          break;
        case Resource:
          assert value.equals(resourcePrefix + "o1");
          break;
        default:
          assert value.equals("string");
      }
    }

    triples = connector.getTriples(SUBJECT, ontologyPrefix + "p2");
    assert triples.size() == 1;
    assert triples.get(0).getObject().getValue().equals("string");

    assert connector.removeResource(SUBJECT, ontologyPrefix + "p1", resourcePrefix + "o1");
    assert connector.removeLiteral(SUBJECT, ontologyPrefix + "p2", "string");
    assert connector.removeLiteral(SUBJECT, ontologyPrefix + "p1", 0.2);
    triples = connector.getTriplesOfSubject(SUBJECT);
    assert triples.isEmpty();
  }
}
