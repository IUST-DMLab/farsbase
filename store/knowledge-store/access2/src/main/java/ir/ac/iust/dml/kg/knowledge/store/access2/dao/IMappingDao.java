package ir.ac.iust.dml.kg.knowledge.store.access2.dao;

import ir.ac.iust.dml.kg.knowledge.commons.PagingList;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.PropertyMapping;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.TemplateMapping;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * interface for read and write TemplateMapping
 */
public interface IMappingDao {
    void write(TemplateMapping... mappings);

    void delete(TemplateMapping... mappings);

    TemplateMapping read(ObjectId id);

    TemplateMapping read(String title);

    PagingList<TemplateMapping> readTemplate(Boolean hasTemplateMapping, Boolean hasPropertyMapping, int page, int pageSize);

    PagingList<TemplateMapping> searchTemplate(String template, int page, int pageSize);

    PagingList<PropertyMapping> searchProperty(String template, String property, int page, int pageSize);

    List<String> searchPredicate(String predicate, int max);
}
