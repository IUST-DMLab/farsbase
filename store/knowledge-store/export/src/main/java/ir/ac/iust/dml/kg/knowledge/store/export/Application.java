package ir.ac.iust.dml.kg.knowledge.store.export;

import ir.ac.iust.dml.kg.knowledge.commons.PagingList;
import ir.ac.iust.dml.kg.knowledge.core.TypedValue;
import ir.ac.iust.dml.kg.knowledge.core.ValueType;
import ir.ac.iust.dml.kg.knowledge.store.access2.dao.IOntologyDao;
import ir.ac.iust.dml.kg.knowledge.store.access2.dao.ISubjectDao;
import ir.ac.iust.dml.kg.knowledge.store.access2.dao.IVersionDao;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.Ontology;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.Subject;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.TripleObject;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.Version;
import ir.ac.iust.dml.kg.raw.utils.URIs;
import ir.ac.iust.dml.kg.raw.utils.UriChecker;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ImportResource;
import virtuoso.rdf4j.driver.VirtuosoRepository;
import virtuoso.rdf4j.driver.VirtuosoRepositoryConnection;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 * <p>
 * Export data from mongo to virtuoso
 */
@SuppressWarnings({"SpringAutowiredFieldsWarningInspection", "Duplicates"})
@SpringBootApplication
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@ImportResource({"classpath:persistence-context2.xml"})
public class Application implements CommandLineRunner {
    private static final int PAGE_SIZE = 20000;
    @Autowired
    private IOntologyDao ontologyDao;
    @Autowired
    private ISubjectDao subjectDao;
    @Autowired
    private IVersionDao versionDao;


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args).close();
    }

    @Override
    public void run(String... strings) throws Exception {
        final long start = System.currentTimeMillis();
        System.out.println("Task started at " + new Date());
        System.out.println("You can this command with these parameters:");
        System.out.println("<ttl folder> <virtuoso host>  <virtuoso port> <user> <password> <graph> <move to graph>");
        if (strings.length < 2) {
            System.out.println("You must enter at least 2 first parameters:");
            System.out.println("first parameter is ttl folder. send `skip` if you want to skip ttl creation.");
            System.out.println("second parameter is virtuoso server address. " +
                    "send `skip` if you want to skip virtuoso export.");
            return;
        }
        final Path outputPath = strings[0].equals("skip") ? null : Paths.get(strings[0]);
        if (outputPath != null && !Files.exists(outputPath))
            Files.createDirectories(outputPath);
        final String virtuosoHost = strings[1].equals("skip") ? null : strings[1];
        final String port = strings.length > 2 ? strings[2] : "1111";
        final String user = strings.length > 3 ? strings[3] : "dba";
        final String password = strings.length > 4 ? strings[4] : "fkgVIRTUOSO2017";
        final String graph = strings.length > 5 ? strings[5] : "http://" + System.currentTimeMillis() + ".fkg.iust.ir";
        final String moveToGraph = strings.length > 6 ? strings[6] : null;
        final List<String> subjects;
        final Path subjectsFile = Paths.get("subjects.txt");
        if (Files.exists(subjectsFile)) {
            subjects = Files.readAllLines(subjectsFile);
        } else subjects = null;

        RepositoryConnection con = null;
        if (virtuosoHost != null) {
            System.out.println("Connecting to virtuoso: " + virtuosoHost + ":" + port + " default graph is " + graph);
            VirtuosoRepository repository =
                    new VirtuosoRepository("jdbc:virtuoso://" + virtuosoHost + ":" + port + "/",
                            user, password, graph);
            con = repository.getConnection();
            con.clear(SimpleValueFactory.getInstance().createIRI(graph));
        }
        exportOntology(outputPath, con, 0, 10, graph);
        System.out.println("time elapsed: " + (System.currentTimeMillis() - start) + " milliseconds");
        if (subjects == null) exportAllTriples(outputPath, con, 10, 100, graph);
        else exportTriplesOfSubject(outputPath, con, 10, 100, graph, subjects);
        System.out.println("time elapsed: " + (System.currentTimeMillis() - start) + " milliseconds");
        if (con != null) {
            if (moveToGraph != null) {
                con.clear(SimpleValueFactory.getInstance().createIRI(moveToGraph));
                ((VirtuosoRepositoryConnection) con)
                        .executeSPARUL(String.format("MOVE <%s> TO <%s>", graph, moveToGraph));
                con.clear(SimpleValueFactory.getInstance().createIRI(graph));
            }
            con.close();
        }
        System.out.println("time elapsed: " + (System.currentTimeMillis() - start) + " milliseconds");
        System.out.println("bye bye.");
    }

    @SuppressWarnings("SameParameterValue")
    private void exportTriplesOfSubject(Path ttlFolder, RepositoryConnection con, float minProgress, float maxProgress,
                                        String tempGraph, List<String> subjects) {
        Map<String, Version> versionMap = new HashMap<>();
        versionDao.readAll().forEach(i -> versionMap.put(i.getModule(), i));

        printProgress(0, minProgress, maxProgress);
        List<Subject> result = new ArrayList<>();
        for (String subject : subjects) {
            final Subject subjectTriples = subjectDao.read(URIs.INSTANCE.getDefaultContext(), subject);
            result.add(subjectTriples);
        }
        exportTriples(ttlFolder == null ? null : ttlFolder.resolve("selected_subjects.ttl"),
                con, tempGraph, versionMap, result);
        printProgress(1, minProgress, maxProgress);
    }

    @SuppressWarnings("SameParameterValue")
    private void exportAllTriples(Path ttlFolder, RepositoryConnection con, float minProgress, float maxProgress, String tempGraph) {
        Map<String, Version> versionMap = new HashMap<>();
        versionDao.readAll().forEach(i -> versionMap.put(i.getModule(), i));

        printProgress(0, minProgress, maxProgress);
        PagingList<Subject> result = null;
        do {
            result = subjectDao.readAll(result == null ? 0 : result.getPage() + 1, PAGE_SIZE);
            if (!result.getData().isEmpty())
                exportTriples(ttlFolder == null ? null : ttlFolder.resolve("triples_" + result.getPage() + ".ttl"),
                        con, tempGraph, versionMap, result.getData());
            printProgress((float) (result.getPage()) / result.getPageCount(), minProgress, maxProgress);
        } while (result.getPage() < result.getPageCount());
        printProgress(1, minProgress, maxProgress);
    }

    private void exportTriples(Path ttlFile, RepositoryConnection con, String tempGraph, Map<String, Version> versionMap,
                               List<Subject> result) {
        final ModelBuilder builder = new ModelBuilder();
        final ModelBuilder g = builder.namedGraph(tempGraph);
        final URIs uris = URIs.INSTANCE;
        for (Subject s : result) {
            int relationIndex = 0;
            for (String p : s.getTriples().keySet()) {
                final ArrayList<TripleObject> allObjects = s.getTriples().get(p);
                final List<TypedValue> acceptedValues = new ArrayList<>();
                final Map<String, Map<String, TypedValue>> acceptedProperties = new HashMap<>();
                for (TripleObject o : allObjects) {
                    if (o.getSource() == null || o.getSource().getModule() == null) continue;
                    final Version activeVersion = versionMap.get(o.getSource().getModule());
                    if (activeVersion == null || activeVersion.getActiveVersion() == null
                            || o.getSource().getVersion() != null &&
                            o.getSource().getVersion() >= activeVersion.getActiveVersion()) {
                        final String key = o.toString();
                        if (acceptedProperties.containsKey(key)) {
                            acceptedProperties.get(key).putAll(o.getProperties());
                        } else {
                            acceptedValues.add(o);
                            acceptedProperties.put(key, o.getProperties());
                        }
                    }
                }
                for (TypedValue o : acceptedValues) {
                    final String key = o.toString();
                    final Map<String, TypedValue> properties = acceptedProperties.get(key);
                    if (properties.isEmpty()) {
                        if (hasValidURIs(s.getSubject(), p, o))
                            g.add(s.getSubject(), p, createValue(o));
                    } else {
                        final String relation = s.getSubject() + "/relation_" + relationIndex++;
                        final TypedValue relationValue = new TypedValue(ValueType.Resource, relation);
                        if (hasValidURIs(s.getSubject(), p, relationValue)) {
                            g.add(s.getSubject(), uris.getRelatedPredicates(), createValue(relationValue))
                                    .add(relation, uris.getType(),
                                            SimpleValueFactory.getInstance().createIRI(uris.getRelatedPredicatesClass()))
                                    .add(relation, uris.getMainPredicate(),
                                            SimpleValueFactory.getInstance().createIRI(p))
                                    .add(relation, p, createValue(o));
                            for (Map.Entry<String, TypedValue> prop : properties.entrySet()) {
                                if (hasValidURIs(relation, prop.getKey(), prop.getValue()))
                                    g.add(relation, prop.getKey(),
                                            createValue(prop.getValue()));
                            }
                        }
                    }
                }
            }
        }
        writeModelBuilder(con, builder, ttlFile);
    }

    private void exportOntology(Path ttlFolder, RepositoryConnection con, float minProgress, float maxProgress, String tempGraph) {
        printProgress(0, minProgress, maxProgress);
        PagingList<Ontology> result = null;
        do {
            result = ontologyDao.search(null, null, null, null,
                    null, null, null, null, null,
                    result == null ? 0 : result.getPage() + 1, PAGE_SIZE);
            if (!result.getData().isEmpty()) {
                final ModelBuilder builder = new ModelBuilder();
                for (Ontology o : result.getData())
                    if (hasValidURIs(o))
                        builder.namedGraph(tempGraph).add(o.getSubject(), o.getPredicate(), createValue(o.getObject()));
                writeModelBuilder(con, builder,
                        ttlFolder == null ? null : ttlFolder.resolve("ontology_" + result.getPage() + ".ttl"));
            }
            printProgress((float) (result.getPage()) / result.getPageCount(), minProgress, maxProgress);
        } while (result.getPage() < result.getPageCount());
        printProgress(1, minProgress, maxProgress);
    }

    private void writeModelBuilder(RepositoryConnection con, ModelBuilder builder, Path ttlPath) {
        final Model model = builder.build();
        if (ttlPath == null) {
            if (con != null) runVirtuosoCommand(() -> con.add(model));
            return;
        }
        final FileOutputStream out;
        try {
            out = new FileOutputStream(ttlPath.toFile());
            final RDFWriter writer = Rio.createWriter(RDFFormat.TURTLE, out);
            writer.startRDF();
            writer.handleNamespace(URIs.INSTANCE.getFkgResourcePrefix(),
                    URIs.INSTANCE.prefixedToUri(URIs.INSTANCE.getFkgResourcePrefix() + ":"));
            writer.handleNamespace(URIs.INSTANCE.getFkgNotMappedPropertyPrefix(),
                    URIs.INSTANCE.prefixedToUri(URIs.INSTANCE.getFkgNotMappedPropertyPrefix() + ":"));
            writer.handleNamespace(URIs.INSTANCE.getFkgCategoryPrefix(),
                    URIs.INSTANCE.prefixedToUri(URIs.INSTANCE.getFkgCategoryPrefix() + ":"));
            writer.handleNamespace(URIs.INSTANCE.getFkgDataTypePrefix(),
                    URIs.INSTANCE.prefixedToUri(URIs.INSTANCE.getFkgDataTypePrefix() + ":"));
            writer.handleNamespace(URIs.INSTANCE.getFkgOntologyPrefix(),
                    URIs.INSTANCE.prefixedToUri(URIs.INSTANCE.getFkgOntologyPrefix() + ":"));
            writer.handleNamespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
            writer.handleNamespace("skos", "http://www.w3.org/2004/02/skos/core#");
            writer.handleNamespace("foaf", "http://xmlns.com/foaf/0.1/");
            writer.handleNamespace("owl", "http://www.w3.org/2002/07/owl#");
            writer.handleNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
            writer.handleNamespace("dct", "http://dublincore.org/2012/06/14/dcterms#");
            writer.handleNamespace("dbpm", "http://mappings.dbpedia.org/index.php/");
            model.forEach(writer::handleStatement);
            writer.endRDF();
            if (con != null)
                runVirtuosoCommand(() -> con.add(new FileInputStream(ttlPath.toFile()),
                        URIs.INSTANCE.getDefaultContext(), RDFFormat.TURTLE));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    interface ThrowableRunnable {
        void run() throws Throwable;
    }

    private void runVirtuosoCommand(ThrowableRunnable fun) {
        while (true) {
            try {
                fun.run();
                break;
            } catch (RepositoryException exp) {
                // common error:
                // Caused by: org.eclipse.rdf4j.repository.RepositoryException: java.sql.BatchUpdateException:
                // SR325: Transaction aborted due to a database checkpoint or database-wide atomic operation.
                // Please retry transaction
                exp.printStackTrace();
                if (!exp.getMessage().contains("retry transaction"))
                    break;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    private void printProgress(float val, float minProgress, float maxProgress) {
        System.out.println("#progress " + (minProgress + val * (maxProgress - minProgress)));
    }

    private boolean hasValidURIs(Ontology ontology) {
        return hasValidURIs(ontology.getSubject(), ontology.getPredicate(), ontology.getObject());
    }

    private final UriChecker uriChecker = UriChecker.INSTANCE;

    private boolean hasValidURIs(String subject, String predicate, TypedValue object) {
        return uriChecker.cachedCheckUri(subject) &&
                uriChecker.cachedCheckUri(predicate) &&
                (object.getType() != ValueType.Resource || uriChecker.cachedCheckUri(object.getValue()));
    }

    private Object createValue(TypedValue v) {
        final ValueFactory vf = SimpleValueFactory.getInstance();
        if (v.getType() != null)
            switch (v.getType()) {
                case Resource:
                    return vf.createIRI(v.getValue());
                case String:
                    return vf.createLiteral(v.getValue(), v.getLang());
                case Boolean:
                    return vf.createLiteral(v.getValue(), XMLSchema.BOOLEAN);
                case Byte:
                    return vf.createLiteral(v.getValue(), XMLSchema.BYTE);
                case Short:
                    return vf.createLiteral(v.getValue(), XMLSchema.SHORT);
                case Integer:
                    return vf.createLiteral(v.getValue(), XMLSchema.INTEGER);
                case Long:
                    return vf.createLiteral(v.getValue(), XMLSchema.LONG);
                case Double:
                    return vf.createLiteral(v.getValue(), XMLSchema.DOUBLE);
                case Float:
                    return vf.createLiteral(v.getValue(), XMLSchema.FLOAT);
                case Date:
                    return vf.createLiteral(new Date(Long.parseLong(v.getValue())));

            }
        return vf.createLiteral(v.getValue());
    }
}
