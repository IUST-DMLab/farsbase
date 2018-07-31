package ir.ac.iust.dml.kg.resource.extractor.readers;

import ir.ac.iust.dml.kg.resource.extractor.IResourceReader;
import ir.ac.iust.dml.kg.resource.extractor.Resource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.rdf4j.model.Graph;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.GraphImpl;
import org.eclipse.rdf4j.rio.*;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by Majid Asgari Bidhendi
 * <p>
 * Read entity from virtuoso
 */
public class ResourceReaderFromTTLs implements IResourceReader {
  private static final Logger LOGGER = LogManager.getLogger(ResourceReaderFromTTLs.class);
  private final RDFParser rdfParser = Rio.createParser(RDFFormat.TURTLE);
  private String context;
  private Iterator<Statement> lastReadTTL;
  private List<Path> ttls = new ArrayList<>();
  private int lastTTLFileIndex = 0;
  private Resource last = null; //current reading resource
  private int page = 0;

  public ResourceReaderFromTTLs(String folderAddress, String context) {
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(folderAddress))) {
      for (Path aStream : stream) {
        ttls.add(aStream);
      }
      this.context = context;
      lastReadTTL = lastTTL();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Iterator<Statement> lastTTL() {
    Graph myGraph = new GraphImpl();
    StatementCollector collector = new StatementCollector(myGraph);
    rdfParser.setRDFHandler(collector);
    try {
      rdfParser.parse(new InputStreamReader(new FileInputStream(ttls.get(lastTTLFileIndex++).toFile()), "UTF-8"), context);
    } catch (IndexOutOfBoundsException | IOException | RDFHandlerException | RDFParseException e) {
      return null;
    }
    LOGGER.info("Read page " + page++);
    return collector.getStatements().iterator();
  }

  private boolean hasNext() {
    if (lastReadTTL == null) return false;
    if (lastReadTTL.hasNext()) return true;
    lastReadTTL = lastTTL();
    return lastReadTTL != null && hasNext();
  }

  private Statement next() {
    return lastReadTTL.next();
  }

  @Override
  public List<Resource> read(int pageSize) throws IOException {
    final List<Resource> resources = new ArrayList<>();
    for (int i = 0; i < pageSize && hasNext(); i++) {
      Statement d = next();
      if (d == null) break;
      final String subject = d.getSubject().toString();
      final String predicate = d.getPredicate().toString();
      final String value = d.getObject().stringValue();
      if (last == null || !last.getIri().equals(subject)) {
        if (last != null && last.hasData())
          resources.add(last);
        last = new Resource(subject);
      }
      ResourceDataFiller.fill(last, predicate, value, null);
    }

    if (!hasNext()) {
      if (last != null && last.hasData())
        resources.add(last);
    }
    return resources;
  }

  @Override
  public Boolean isFinished() {
    return !hasNext();
  }

  @Override
  public void close() throws Exception {

  }
}
