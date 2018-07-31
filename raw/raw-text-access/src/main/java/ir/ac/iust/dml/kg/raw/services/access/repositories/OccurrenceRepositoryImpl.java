/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.services.access.repositories;

import ir.ac.iust.dml.kg.raw.services.access.entities.KeyAndCount;
import ir.ac.iust.dml.kg.raw.services.access.entities.Occurrence;
import ir.ac.iust.dml.kg.raw.services.access.entities.User;
import ir.ac.iust.dml.kg.raw.services.access.entities.UserAndCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class OccurrenceRepositoryImpl implements OccurrenceRepositoryCustom {

  @Autowired
  private MongoTemplate op;

  @Override
  public Page<Occurrence> search(int page, int pageSize, String predicate, boolean like,
                                 Integer minOccurrence, Boolean approved,
                                 Boolean assignee, User assigneeUser) {
    Query query = new Query();
    if (predicate != null && like) query.addCriteria(Criteria.where("predicate").regex(predicate));
    if (predicate != null && !like) query.addCriteria(Criteria.where("predicate").is(predicate));
    if (minOccurrence != null) query.addCriteria(Criteria.where("occurrence").gte(minOccurrence));
    if (approved != null) query.addCriteria(Criteria.where("approved").is(approved));
    if (assignee != null) query.addCriteria(Criteria.where("assignee").exists(assignee));
    if (assigneeUser != null) query.addCriteria(Criteria.where("assignee").is(assigneeUser));
    query.with(new Sort(
        new Sort.Order(Sort.Direction.DESC, "selectedByUser"),
        new Sort.Order(Sort.Direction.DESC, "occurrence")));
    return page(op, query, page, pageSize, Occurrence.class);
  }

  @Override
  public Page<KeyAndCount> predicates(int page, int pageSize, String predicate) {
    final GroupOperation group = Aggregation.group("predicate").count()
        .as("count");
    final MatchOperation match = (predicate == null) ? null :
        Aggregation.match(Criteria.where("predicate").regex(predicate));

    final GroupOperation totalCountGroup = Aggregation.group().count().as("count");
    final Aggregation countAgg = (match != null) ?
        Aggregation.newAggregation(match, group, totalCountGroup) :
        Aggregation.newAggregation(group, totalCountGroup);
    final long totalCount = op.aggregate(countAgg, Occurrence.class, KeyAndCount.class)
        .getMappedResults().get(0).getCount();

    final SkipOperation skip = Aggregation.skip((long) page * pageSize);
    final LimitOperation limit = Aggregation.limit(pageSize);
    final SortOperation sort = Aggregation.sort(Sort.Direction.DESC, "count");

    final Aggregation agg = (match != null) ?
        Aggregation.newAggregation(match, group, sort, skip, limit) :
        Aggregation.newAggregation(group, sort, skip, limit);

    final AggregationResults<KeyAndCount> result =
        op.aggregate(agg, Occurrence.class, KeyAndCount.class);

    return new PageImpl<>(result.getMappedResults(),
        new PageRequest(page, pageSize), totalCount);
  }

  @Override
  public List<UserAndCount> assignees(String predicate) {
    final GroupOperation group = Aggregation.group("assignee").count()
        .as("count");
    final MatchOperation match = Aggregation.match(Criteria.where("predicate").regex(predicate));

    return op.aggregate(Aggregation.newAggregation(match, group),
        Occurrence.class, UserAndCount.class).getMappedResults();
  }

  public static <T> Page<T> page(MongoTemplate op, Query query, int page, int pageSize, Class<T> entityClass) {
    final long total = op.count(query, entityClass);
    final PageRequest p = new PageRequest(page, pageSize);
    query.with(p);
    final List<T> list = op.find(query, entityClass);
    return new PageImpl<>(list, p, total);
  }
}
