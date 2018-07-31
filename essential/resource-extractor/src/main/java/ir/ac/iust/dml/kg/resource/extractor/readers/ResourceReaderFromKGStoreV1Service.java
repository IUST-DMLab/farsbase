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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Read entity from KGStore Service [/rs/v1/triples/search]
 */
public class ResourceReaderFromKGStoreV1Service implements IResourceReader {
    private static final Logger LOGGER = LogManager.getLogger(ResourceReaderFromKGStoreV1Service.class);
    private final WebClient client;
    private int lastPage = 0;
    private Resource last = null; //current reading resource


    public ResourceReaderFromKGStoreV1Service(String baseUrl) {
        this.client = WebClient.create(baseUrl, Collections.singletonList(new JacksonJsonProvider()));
    }

    @Override
    public Boolean isFinished() {
        return lastPage == -1;
    }

    @Override
    public List<Resource> read(int pageSize) throws IOException {
        final List<Resource> resources = new ArrayList<>();
        if (lastPage == -1) return resources;
        final Response response = client.reset().path("/rs/v1/triples/search")
                .query("page", lastPage).query("pageSize", pageSize)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();
        if (response.getStatus() == 200) {
            final PagingResourceData result = response.readEntity(PagingResourceData.class);
            LOGGER.info("Read page " + result.page + " from " + result.pageCount);
            for (TripleData d : result.data) {
                if (last == null || !last.getIri().equals(d.subject)) {
                    if (last != null && last.hasData())
                        resources.add(last);
                    last = new Resource(d.subject);
                }
              ResourceDataFiller.fill(last, d.predicate, d.object.value, d.object.lang);
            }
            lastPage++;
            if (lastPage > result.pageCount) {
                if (last != null && last.hasData())
                    resources.add(last);
                lastPage = -1; //do not continue
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
    static class TripleData {
        public String subject;
        public String predicate;
        public ValueData object;
    }

    @SuppressWarnings("WeakerAccess")
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ValueData {
        public String value;
        public String lang;
    }

    @SuppressWarnings("WeakerAccess")
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class PagingResourceData {
        public TripleData[] data;
        public int page;
        public int pageCount;
    }

}
