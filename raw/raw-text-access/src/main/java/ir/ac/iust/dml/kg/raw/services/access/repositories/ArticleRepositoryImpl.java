/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.services.access.repositories;

import ir.ac.iust.dml.kg.raw.services.access.entities.Article;
import ir.ac.iust.dml.kg.raw.services.access.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class ArticleRepositoryImpl implements ArticleRepositoryCustom {

  @Autowired
  private MongoTemplate op;

  @Override
  public Page<Article> search(int page, int pageSize, String path, String title,
                              Integer minPercentOfRelations, Boolean approved,
                              User selectedByUser) {
    Query query = new Query();
    if (path != null) query.addCriteria(Criteria.where("path").regex(path));
    if (title != null) query.addCriteria(Criteria.where("title").regex(title));
    if (selectedByUser != null) query.addCriteria(Criteria.where("selectedByUser").is(selectedByUser));
    if (minPercentOfRelations != null)
      query.addCriteria(Criteria.where("percentOfRelations").gte(minPercentOfRelations));
    if (approved != null) query.addCriteria(Criteria.where("approved").is(approved));
    query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "percentOfRelations")));
    return OccurrenceRepositoryImpl.page(op, query, page, pageSize, Article.class);
  }
}
