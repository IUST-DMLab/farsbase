package ir.ac.iust.dml.kg.knowledge.store.client;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.cxf.jaxrs.client.WebClient;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * ‌ Client to use knowledge store service /rs//v1/experts
 */
public class V1StoreClient {
    private final WebClient client;
    private int lastPage = 0;

    public V1StoreClient(String url) {
        this.client = WebClient.create(url, Collections.singletonList(new JacksonJsonProvider()));
    }

    public List<Triple> triplesSubject(String sourceModule, String expert, String subjectQuery, String subjectMatch, Integer size) {
        WebClient req = client.reset().path("/rs/v1/experts/triples/subject");
        if (sourceModule != null)
            req = req.query("sourceModule", sourceModule);
        if (subjectQuery != null)
            req = req.query("subjectQuery", subjectQuery);
        if (subjectMatch != null)
            req = req.query("subjectMatch", subjectMatch);
        if (size != null)
            req = req.query("size", size);
        final Collection<? extends Triple> triples = req
                .query("module", "web")
                .query("expert", expert)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .getCollection(Triple.class);
        final List<Triple> result = new ArrayList<>();
        triples.forEach(result::add);
        return result;
    }

    public List<Triple> triples(String expert, int count) {
        final Collection<? extends Triple> triples = client.reset().path("/rs/v1/experts/triples")
                .query("module", "web")
                .query("expert", expert)
                .query("count", count)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .getCollection(Triple.class);
        final List<Triple> result = new ArrayList<>();
        triples.forEach(result::add);
        return result;
    }

    public Boolean vote(String identifier, String expert, Vote vote) {
        return client.reset().path("/rs/v1/experts/vote")
                .query("identifier", identifier)
                .query("module", "web")
                .query("expert", expert)
                .query("vote", vote)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(Boolean.class);
    }


}

