package ir.ac.iust.dml.kg.knowledge.expert.access.mongo;

import ir.ac.iust.dml.kg.knowledge.commons.MongoDaoUtils;
import ir.ac.iust.dml.kg.knowledge.commons.PagingList;
import ir.ac.iust.dml.kg.knowledge.expert.access.dao.IReportDao;
import ir.ac.iust.dml.kg.knowledge.expert.access.entities.Ticket;
import ir.ac.iust.dml.kg.knowledge.expert.access.entities.User;
import ir.ac.iust.dml.kg.knowledge.expert.access.stats.KeyCount;
import ir.ac.iust.dml.kg.knowledge.expert.access.stats.UserStats;
import ir.ac.iust.dml.kg.knowledge.expert.access.stats.UserVoteStats;
import ir.ac.iust.dml.kg.knowledge.store.client.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 */
@Repository
public class ReportDaoImpl implements IReportDao {
    @Autowired
    private MongoOperations op;

    @Override
    public PagingList<Ticket> searchTicketState(User user, String subject,
                                                Boolean hasVote, Vote vote,
                                                int page, int pageSize) {
        final Query query = new Query();
        if (user != null)
            query.addCriteria(Criteria.where("user").is(user));
        if (vote != null)
            query.addCriteria(Criteria.where("vote").is(vote));
        else if (hasVote != null)
            query.addCriteria(Criteria.where("vote").exists(hasVote));
        if (subject != null)
            query.addCriteria(Criteria.where("triple.subject").is(subject));
        return MongoDaoUtils.paging(op, Ticket.class, query, page, pageSize);
    }

    @Override
    public PagingList<KeyCount> countBySubject(User user,
                                               Boolean hasVote, Vote vote,
                                               int page, int pageSize) {
        final List<AggregationOperation> operations = new ArrayList<>();
        if (user != null)
            operations.add(Aggregation.match(Criteria.where("user").is(user)));
        if (vote != null)
            operations.add(Aggregation.match(Criteria.where("vote").is(vote)));
        else if (hasVote != null)
            operations.add(Aggregation.match(Criteria.where("vote").exists(hasVote)));
        operations.add(Aggregation.group("triple.subject").count().as("count"));
        return MongoDaoUtils
                .aggregate(op, Ticket.class, KeyCount.class, page, pageSize, operations.toArray(new AggregationOperation[operations.size()]));
    }

    @Override
    public PagingList<UserStats> countByUser(User user,
                                             Boolean hasVote, Vote vote,
                                             int page, int pageSize) {
        final List<AggregationOperation> operations = new ArrayList<>();
        if (user != null)
            operations.add(Aggregation.match(Criteria.where("user").is(user)));
        if (vote != null)
            operations.add(Aggregation.match(Criteria.where("vote").is(vote)));
        else if (hasVote != null)
            operations.add(Aggregation.match(Criteria.where("vote").exists(hasVote)));
        operations.add(Aggregation.group("user").count().as("count"));
        return MongoDaoUtils
                .aggregate(op, Ticket.class, UserStats.class, page, pageSize, operations.toArray(new AggregationOperation[operations.size()]));
    }

    @Override
    public PagingList<UserVoteStats> countByUserVote(User user,
                                                     Boolean hasVote, Vote vote,
                                                     int page, int pageSize) {
        final List<AggregationOperation> operations = new ArrayList<>();
        if (user != null)
            operations.add(Aggregation.match(Criteria.where("user").is(user)));
        if (vote != null)
            operations.add(Aggregation.match(Criteria.where("vote").is(vote)));
        else if (hasVote != null)
            operations.add(Aggregation.match(Criteria.where("vote").exists(hasVote)));
        operations.add(Aggregation.group("user", "vote").count().as("count"));
        return MongoDaoUtils
                .aggregate(op, Ticket.class, UserVoteStats.class, page, pageSize, operations.toArray(new AggregationOperation[operations.size()]));
    }
}
