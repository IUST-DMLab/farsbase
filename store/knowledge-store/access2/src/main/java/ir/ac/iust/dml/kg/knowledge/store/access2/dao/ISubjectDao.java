package ir.ac.iust.dml.kg.knowledge.store.access2.dao;


import ir.ac.iust.dml.kg.knowledge.commons.PagingList;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.Subject;
import org.bson.types.ObjectId;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 * <p>
 * Dao for read and writes subject
 */
public interface ISubjectDao {
    void createIndex(String... fileds);

    void write(Subject... subjects);

    void delete(Subject... subjects);

    Subject read(ObjectId id);

    Subject read(String context, String subject);

    Subject randomSubjectForExpert(String source, String voter);

    PagingList<Subject> searchHasPredicate(String predicate, int page, int pageSize);

    PagingList<Subject> searchHasValue(String predicate, String object, int page, int pageSize);

    PagingList<Subject> readAll(int page, int pageSize);
}
