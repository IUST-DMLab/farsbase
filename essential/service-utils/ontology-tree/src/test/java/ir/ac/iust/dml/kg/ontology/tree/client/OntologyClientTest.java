package ir.ac.iust.dml.kg.ontology.tree.client;

import org.junit.Test;

public class OntologyClientTest {
  @Test
  public void match() throws Exception {
    OntologyClient client = new OntologyClient("http://194.225.227.161:8090");
    final PagedData<OntologyClass> result = client.search(0, 10, null, null, false);
    assert !result.getData().isEmpty();
  }

}