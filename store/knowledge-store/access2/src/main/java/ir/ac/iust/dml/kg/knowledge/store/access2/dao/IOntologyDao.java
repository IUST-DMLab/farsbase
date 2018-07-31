package ir.ac.iust.dml.kg.knowledge.store.access2.dao;

import ir.ac.iust.dml.kg.knowledge.commons.PagingList;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.Ontology;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.TripleState;
import org.bson.types.ObjectId;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Interface for read and write triples
 */
public interface IOntologyDao {
    void write(Ontology... ontology);

    void delete(Ontology... ontology);

    Ontology read(ObjectId id);

    Ontology read(String context, String subject, String predicate, String object);

    PagingList<Ontology> search(String context, Boolean contextLike,
                                String subject, Boolean subjectLike,
                                String predicate, Boolean predicateLike,
                                String object, Boolean objectLike,
                                TripleState state, int page, int pageSize);
}
