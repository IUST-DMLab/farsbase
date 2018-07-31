package ir.ac.iust.dml.kg.knowledge.store.migration;

import ir.ac.iust.dml.kg.knowledge.store.access.entities.Triple;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.Ontology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ImportResource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 * <p>
 * Migrate data from v1 access to v2 access
 */
@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
@SpringBootApplication
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@ImportResource({"classpath:persistence-context.xml", "classpath:persistence-context2.xml"})
public class Application implements CommandLineRunner {
    @Autowired
    private ir.ac.iust.dml.kg.knowledge.store.access.dao.ITripleDao tripleDao;

    @Autowired
    private ir.ac.iust.dml.kg.knowledge.store.access2.dao.IOntologyDao ontologyDao2;


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    private String[] ontologyPredicates = new String[]{
            "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"};
    private String[] ontologyObjects = new String[]{"http://www.w3.org/2002/07/owl#Class",
            "http://www.w3.org/1999/02/22-rdf-syntax-ns#Property"};

    @Override
    public void run(String... strings) throws Exception {
        final Set<String> ontologySubjects = new HashSet<>();
        for (String predicate : ontologyPredicates)
            for (String object : ontologyObjects) {
                final List<Triple> subjects =
                        tripleDao.search(null, null, predicate, object, 0, 0).getData();
                System.out.println(String.format("number of triples: %d for p: %s o: %s", subjects.size(), predicate, object));
                for (Triple triple : subjects)
                    ontologySubjects.add(triple.getSubject());
            }
        int duplicates = 0;
        for (String subject : ontologySubjects) {
            final List<Triple> ontologies =
                    tripleDao.search(null, subject, null, null, 0, 0)
                            .getData();
            System.out.println(String.format("number of triples of %s is %d", subject, ontologies.size()));
            for (Triple o : ontologies) {
                try {
                    ontologyDao2.write(new Ontology(o.getContext(), o.getSubject(), o.getPredicate(), o.getObject()));
                } catch (Throwable ignored) {
                    System.err.println(o.getContext() + " - " + o.getSubject() + "-" + o.getPredicate() + "-" + o.getObject());
                    duplicates++;
                }
            }
        }
        System.out.println("duplicates: " + duplicates);
    }
}
