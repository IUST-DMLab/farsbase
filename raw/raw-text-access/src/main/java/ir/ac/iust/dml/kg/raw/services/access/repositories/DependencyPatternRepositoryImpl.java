/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.services.access.repositories;

import ir.ac.iust.dml.kg.raw.services.access.entities.DependencyPattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class DependencyPatternRepositoryImpl implements DependencyPatternRepositoryCustom {

  @Autowired
  private MongoTemplate op;

  @Override
  public Page<DependencyPattern> search(int page, int pageSize, Integer maxSentenceLength,
                                        Integer minCount, Boolean approved) {
    Query query = new Query();
    if (minCount != null) query.addCriteria(Criteria.where("count").gte(minCount));
    if (maxSentenceLength != null) query.addCriteria(Criteria.where("sentenceLength").lte(maxSentenceLength));
    if (approved != null) query.addCriteria(Criteria.where("relations.predicate").exists(approved));
    query.with(new Sort(
        new Sort.Order(Sort.Direction.DESC, "selectedByUser"),
        new Sort.Order(Sort.Direction.DESC, "count")));
    return OccurrenceRepositoryImpl.page(op, query, page, pageSize, DependencyPattern.class);
  }
}
