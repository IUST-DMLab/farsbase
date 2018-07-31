package ir.ac.iust.dml.kg.knowledge.expert.access.mongo;

import ir.ac.iust.dml.kg.knowledge.commons.MongoDaoUtils;
import ir.ac.iust.dml.kg.knowledge.commons.PagingList;
import ir.ac.iust.dml.kg.knowledge.expert.access.dao.ITicketDao;
import ir.ac.iust.dml.kg.knowledge.expert.access.entities.Ticket;
import ir.ac.iust.dml.kg.knowledge.expert.access.entities.User;
import ir.ac.iust.dml.kg.knowledge.expert.access.stats.KeyCount;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * impl {@link ITicketDao}
 */
@Repository
public class TicketDaoImpl implements ITicketDao {
    @Autowired
    private MongoOperations op;

    @Override
    public void write(Ticket... tickets) {
        for (Ticket t : tickets)
            op.save(t);
    }

    @Override
    public void delete(Ticket... tickets) {
        for (Ticket t : tickets)
            op.remove(t);
    }

    @Override
    public Ticket read(ObjectId id) {
        return op.findOne(
                new Query().addCriteria(Criteria.where("id").is(id)),
                Ticket.class
        );
    }

    @Override
    public Ticket read(User user, String identifier) {
        final Query query = new Query()
                .addCriteria(Criteria.where("user").is(user))
                .addCriteria(Criteria.where("triple.identifier").is(identifier));
        return op.findOne(query, Ticket.class);
    }

    @Override
    public PagingList<Ticket> readAssignedTicket(User user, String subject, int page, int pageSize) {
        final Query query = new Query()
                .addCriteria(Criteria.where("user").is(user))
                .addCriteria(Criteria.where("vote").exists(false));
        if (subject != null)
            query.addCriteria(Criteria.where("triple.subject").is(subject));
        return MongoDaoUtils.paging(op, Ticket.class, query, page, pageSize);
    }

    @Override
    public PagingList<KeyCount> readAssignedSubjects(User user, int page, int pageSize) {
        final AggregationOperation[] operations = new AggregationOperation[2];
        operations[0] = Aggregation.match(Criteria.where("user").is(user).and("vote").exists(false));
        operations[1] = Aggregation.group("triple.subject").count().as("count");
        return MongoDaoUtils
                .aggregate(op, Ticket.class, KeyCount.class, page, pageSize, operations);
    }
}
