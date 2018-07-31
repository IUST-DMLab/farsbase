package ir.ac.iust.dml.kg.search.feedback.access.repositories;

import ir.ac.iust.dml.kg.search.feedback.access.entities.FeedbackPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;

import java.util.List;

public class FeedbackPostRepositoryImpl implements FeedbackPostRepositoryCustom {

  @Autowired
  private MongoTemplate op;

  @Override
  public Page<FeedbackPost> search(int page, int pageSize,
                                   String textKeyword, String queryKeyword,
                                   Long minSendDate, Long maxSendDate,
                                   Boolean approved, Boolean done) {
    Query query = new Query();
    if (textKeyword != null) query.addCriteria(new TextCriteria().matching(textKeyword));
    if (queryKeyword != null) query.addCriteria(Criteria.where("query").regex(queryKeyword));
    if (minSendDate != null && maxSendDate != null)
      query.addCriteria(Criteria.where("sendTime").gte(minSendDate).lte(maxSendDate));
    if (minSendDate != null && maxSendDate == null) query.addCriteria(Criteria.where("sendTime").gte(minSendDate));
    if (minSendDate == null && maxSendDate != null) query.addCriteria(Criteria.where("sendTime").lte(maxSendDate));
    if (approved != null) query.addCriteria(Criteria.where("approved").is(approved));
    if (done != null) query.addCriteria(Criteria.where("done").is(done));
    query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "sendTime")));
    return page(op, query, page, pageSize, FeedbackPost.class);
  }

  public static <T> Page<T> page(MongoTemplate op, Query query, int page, int pageSize, Class<T> entityClass) {
    final long total = op.count(query, entityClass);
    final PageRequest p = new PageRequest(page, pageSize);
    query.with(p);
    final List<T> list = op.find(query, entityClass);
    return new PageImpl<>(list, p, total);
  }
}
