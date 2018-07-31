package ir.ac.iust.dml.kg.resource.extractor.client;

import org.junit.Test;

import java.util.List;

/**
 * Descriotion of file
 *
 * @feature [issue_url] description
 * @bug [issue_url] description
 */
public class ExtractorClientTest {
    @Test
    public void match() throws Exception {
        ExtractorClient client = new ExtractorClient("http://dmls.iust.ac.ir:8094");
        final List<MatchedResource> result = client.match("من علی لاریجانی نیستم.");
        assert !result.isEmpty();
    }

}