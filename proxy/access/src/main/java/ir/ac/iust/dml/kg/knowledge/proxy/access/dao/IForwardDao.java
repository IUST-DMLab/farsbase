package ir.ac.iust.dml.kg.knowledge.proxy.access.dao;

import ir.ac.iust.dml.kg.knowledge.commons.PagingList;
import ir.ac.iust.dml.kg.knowledge.proxy.access.entities.Forward;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 * <p>
 * Dao for forwards
 */
public interface IForwardDao {
    void write(Forward... forwards);

    void delete(Forward... forwards);

    Forward read(ObjectId id);

    Forward readBySource(String source);

    List<Forward> readAll();

    PagingList<Forward> search(String source, int page, int pageSize);
}
