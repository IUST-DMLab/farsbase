package ir.ac.iust.dml.kg.resource.extractor.readers;

import ir.ac.iust.dml.kg.resource.extractor.IResourceReader;
import ir.ac.iust.dml.kg.resource.extractor.Resource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import virtuoso.rdf4j.driver.VirtuosoRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Read entity from virtuoso
 */
public class ResourceReaderFromVirtuoso implements IResourceReader {
    private static final Logger LOGGER = LogManager.getLogger(ResourceReaderFromVirtuoso.class);
    private final RepositoryConnection con;
    private final TupleQueryResult result;
    private Resource last = null; //current reading resource
    private int page = 0;

    public ResourceReaderFromVirtuoso(String ip, String port, String user, String password, String context) {
        VirtuosoRepository repository = new VirtuosoRepository("jdbc:virtuoso://" + ip + ":" + port + "/",
                user, password);
        con = repository.getConnection();
        TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, "SELECT * FROM <" + context + "> WHERE { ?x ?y ?z }");
        result = query.evaluate();
    }

    @Override
    public List<Resource> read(int pageSize) throws IOException {
        final List<Resource> resources = new ArrayList<>();
        LOGGER.info("Read page " + page++);
        for (int i = 0; i < pageSize && result.hasNext(); i++) {
            BindingSet d = result.next();
            final String subject = d.getBinding("x").getValue().stringValue();
            final String predicate = d.getBinding("y").getValue().stringValue();
            final String value = d.getBinding("z").getValue().stringValue();
            if (last == null || !last.getIri().equals(subject)) {
                if (last != null && last.hasData())
                    resources.add(last);
                last = new Resource(subject);
            }
            ResourceDataFiller.fill(last, predicate, value, null);
        }

        if (!result.hasNext()) {
            if (last != null && last.hasData())
                resources.add(last);
        }
        return resources;
    }

    @Override
    public Boolean isFinished() {
        return !result.hasNext();
    }

    @Override
    public void close() throws Exception {
        con.close();
    }
}
