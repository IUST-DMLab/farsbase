package ir.ac.iust.dml.kg.resource.extractor.readers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import ir.ac.iust.dml.kg.resource.extractor.IResourceReader;
import ir.ac.iust.dml.kg.resource.extractor.Resource;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 * <p>
 * Read entity from KGStore Service [/rs/v1/triples/search]
 */
public class ResourceReaderFromKGStoreV2Service implements IResourceReader {
  private static final Logger LOGGER = LogManager.getLogger(ResourceReaderFromKGStoreV2Service.class);
  private final WebClient client;
  private int lastTriplePage = 0;
  private int lastOntologyPage = 0;
  private Resource last = null; //current reading resource


  public ResourceReaderFromKGStoreV2Service(String baseUrl) {
    this.client = WebClient.create(baseUrl, Collections.singletonList(new JacksonJsonProvider()));
  }

  @Override
  public Boolean isFinished() {
    return lastTriplePage == -1 &&lastOntologyPage == -1;
  }

  @Override
  public List<Resource> read(int pageSize) throws IOException {
    final List<Resource> resources = new ArrayList<>();
    if (lastTriplePage == -1 &&lastOntologyPage == -1) return resources;
    if(lastOntologyPage != -1) {
      final Response response = client.reset().path("/rs/v2/ontology/search")
              .query("page", lastOntologyPage).query("pageSize", pageSize)
              .accept(MediaType.APPLICATION_JSON_TYPE)
              .get();
      if (response.getStatus() == 200) {
        final PagingOntologyData result = response.readEntity(PagingOntologyData.class);
        LOGGER.info("Read ontology page " + result.page + " from " + result.pageCount);
        for (OntologyData d : result.data) {
          if (last == null || !last.getIri().equals(d.subject)) {
            if (last != null && last.hasData())
              resources.add(last);
            last = new Resource(d.subject);
          }
          ResourceDataFiller.fill(last, d.predicate, d.object.value, d.object.lang);
        }
        lastOntologyPage++;
        if (lastOntologyPage > result.pageCount) {
          if (last != null && last.hasData())
            resources.add(last);
          lastOntologyPage = -1;
        }
      }
    } else {
      final Response response = client.reset().path("/rs/v2/subjects/all")
              .query("page", lastTriplePage).query("pageSize", pageSize)
              .accept(MediaType.APPLICATION_JSON_TYPE)
              .get();
      if (response.getStatus() == 200) {
        final PagingSubjectData result = response.readEntity(PagingSubjectData.class);
        LOGGER.info("Read entities page " + result.page + " from " + result.pageCount);
        for (SubjectData d : result.data) {
          last = new Resource(d.subject);
          resources.add(last);
          for(String predicate: d.triples.keySet()) {
            final List<ValueData> values = d.triples.get(predicate);
            for(ValueData valueData: values)
              ResourceDataFiller.fill(last, predicate, valueData.value, valueData.lang);
          }
        }
        lastTriplePage++;
        if (lastTriplePage > result.pageCount) lastTriplePage = -1; //do not continue
      }
    }
    return resources;
  }

  @Override
  public void close() throws Exception {
    client.close();
  }

  @SuppressWarnings("WeakerAccess")
  @JsonIgnoreProperties(ignoreUnknown = true)
  static class ValueData {
    public String value;
    public String lang;
  }

  @SuppressWarnings("WeakerAccess")
  @JsonIgnoreProperties(ignoreUnknown = true)
  static class SubjectData {
    public String subject;
    public Map<String, List<ValueData>> triples;
  }

  @SuppressWarnings("WeakerAccess")
  @JsonIgnoreProperties(ignoreUnknown = true)
  static class PagingSubjectData {
    public SubjectData[] data;
    public int page;
    public int pageCount;
  }

  @SuppressWarnings("WeakerAccess")
  @JsonIgnoreProperties(ignoreUnknown = true)
  static class OntologyData {
    public String subject;
    public String predicate;
    public ValueData object;
  }

  @SuppressWarnings("WeakerAccess")
  @JsonIgnoreProperties(ignoreUnknown = true)
  static class PagingOntologyData {
    public OntologyData[] data;
    public int page;
    public int pageCount;
  }

}
