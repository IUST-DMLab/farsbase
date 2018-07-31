package ir.ac.iust.dml.kg.knowledge.store.access.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import ir.ac.iust.dml.kg.knowledge.commons.MongoDaoUtils;
import ir.ac.iust.dml.kg.knowledge.commons.PagingList;
import ir.ac.iust.dml.kg.knowledge.store.access.dao.ITripleDao;
import ir.ac.iust.dml.kg.knowledge.store.access.entities.Triple;
import ir.ac.iust.dml.kg.knowledge.store.access.entities.TripleState;
import ir.ac.iust.dml.kg.knowledge.store.access.stats.KeyCount;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * impl {@link ITripleDao}
 */
@Deprecated
@Repository
public class TripleDaoImpl implements ITripleDao {
    @Autowired
    @Qualifier("store1")
    private MongoOperations op;

    @Override
    public void write(Triple... triples) {
        for (Triple triple : triples)
            op.save(triple);
    }

    @Override
    public void delete(Triple... triples) {
        for (Triple triple : triples) {
            op.remove(triple);
        }
    }

    @Override
    public Triple read(ObjectId id) {
        return op.findOne(
                new Query().addCriteria(Criteria.where("id").is(id)),
                Triple.class
        );
    }

    @Override
    public Triple read(String context, String subject, String predicate, String object) {
        return op.findOne(
                new Query().addCriteria(Criteria.where("context").is(context))
                        .addCriteria(Criteria.where("subject").is(subject))
                        .addCriteria(Criteria.where("predicate").is(predicate))
                        .addCriteria(Criteria.where("object.value").is(object)),
                Triple.class
        );
    }


    @Override
    public PagingList<Triple> search(String context, String subject, String predicate, String object, int page, int pageSize) {
        return search(context, false, subject, false, predicate, false, object, false, page, pageSize);
    }

    @Override
    public PagingList<Triple> search(
            String context, boolean useRegexForContext,
            String subject, boolean useRegexForSubject,
            String predicate, boolean useRegexForPredicate,
            String object, boolean useRegexForObject,
            int page, int pageSize) {
        final Query query = new Query();
        if (context != null && !useRegexForContext)
            query.addCriteria(Criteria.where("context").is(context));
        else if (context != null)
            query.addCriteria(Criteria.where("context").regex(context));

        if (subject != null && !useRegexForSubject)
            query.addCriteria(Criteria.where("subject").is(subject));
        else if (subject != null)
            query.addCriteria(Criteria.where("subject").regex(subject));

        if (predicate != null && !useRegexForPredicate)
            query.addCriteria(Criteria.where("predicate").is(predicate));
        else if (predicate != null)
            query.addCriteria(Criteria.where("predicate").regex(predicate));

        if (object != null && !useRegexForObject)
            query.addCriteria(Criteria.where("object.value").is(object));
        else if (object != null)
            query.addCriteria(Criteria.where("object.value").regex(object));
        return MongoDaoUtils.paging(op, Triple.class, query, page, pageSize);
    }

    @Override
    public PagingList<Triple> read(TripleState state, Long after, int page, int pageSize) {
        final Query query = new Query();
        if (state != null)
            query.addCriteria(Criteria.where("state").is(state));
        if (after != null)
            query.addCriteria(Criteria.where("modificationEpoch").gte(after));
        return MongoDaoUtils.paging(op, Triple.class, query, page, pageSize);
    }

    @Override
    public List<Triple> randomTripleForExpert(String notModule, String notExpert, int count) {
        final Query query = new Query()
                .addCriteria(Criteria.where("votes.module").ne(notModule))
                .addCriteria(Criteria.where("votes.expert").ne(notExpert))
                .addCriteria(Criteria.where("state").is(TripleState.None))
                .with(new Sort(Sort.Direction.ASC, "subject"));
        final int total = (int) op.count(new Query(), Triple.class);
        final List<Triple> cs = new ArrayList<>();
        final Set<Integer> randomIndexes = randomIndex(count, total);
        for (int index : randomIndexes) {
            final PageRequest pageRequest = new PageRequest(index, 10);
            query.with(pageRequest);
            final List<Triple> list = op.find(query, Triple.class);
            cs.addAll(list);
            if (cs.size() >= count)
                break;
        }
        return cs;
    }

    @Override
    public List<Triple> randomSubjectForExpert(String isSourceModule, String neModule, String neExpert, String subjectRegex,
                                               String subject, Integer size) {
        final List<BasicDBObject> sampleQuery = new ArrayList<>();
        if (subject != null)
            sampleQuery.add(new BasicDBObject("$match", new BasicDBObject("subject", subject)));
        if (size != null)
            sampleQuery.add(new BasicDBObject("$sample", new BasicDBObject("size", size)));
        if (subjectRegex != null)
            sampleQuery.add(new BasicDBObject("$match", new BasicDBObject("subject", new BasicDBObject("$regex", subjectRegex))));
        if (isSourceModule != null)
            sampleQuery.add(new BasicDBObject("$match", new BasicDBObject("sources.module", isSourceModule)));
        if (neModule != null)
            sampleQuery.add(new BasicDBObject("$match", new BasicDBObject("votes.module", new BasicDBObject("$ne", neModule))));
        if (neExpert != null)
            sampleQuery.add(new BasicDBObject("$match", new BasicDBObject("votes.expert", new BasicDBObject("$ne", neExpert))));
        sampleQuery.add(new BasicDBObject("$match", new BasicDBObject("state", TripleState.None.toString())));
        sampleQuery.add(new BasicDBObject("$sample", new BasicDBObject("size", 1)));

        final Iterator<DBObject> subjectAggregate = op.getCollection("triples").aggregate(sampleQuery).results().iterator();
        if (!subjectAggregate.hasNext()) return new ArrayList<>();
        final Triple selectedSubject = op.getConverter().read(Triple.class, subjectAggregate.next());
        if (selectedSubject == null) return new ArrayList<>();
        final Query query = new Query().addCriteria(Criteria.where("subject").is(selectedSubject.getSubject()));
        if (isSourceModule != null)
            query.addCriteria(Criteria.where("sources.module").is(isSourceModule));
        if (neModule != null)
            query.addCriteria(Criteria.where("votes.module").ne(neModule));
        if (neExpert != null)
            query.addCriteria(Criteria.where("votes.expert").ne(neExpert));
        query.addCriteria(Criteria.where("state").is(TripleState.None));
        return op.find(query, Triple.class);
    }

    @Override
    public PagingList<KeyCount> searchSubjectForExpert(String isSourceModule, String neModule, String neExpert, String subject, int page, int pageSize) {
        final List<AggregationOperation> operations = new ArrayList<>();
        if (isSourceModule != null)
            operations.add(Aggregation.match(Criteria.where("sources.module").is(isSourceModule)));
        if (neModule != null)
            operations.add(Aggregation.match(Criteria.where("votes.module").ne(neModule)));
        if (neExpert != null)
            operations.add(Aggregation.match(Criteria.where("votes.expert").ne(neExpert)));
        if (subject != null)
            operations.add(Aggregation.match(Criteria.where("subject").regex(subject)));
        operations.add(Aggregation.group("subject").count().as("count"));
        return MongoDaoUtils
                .aggregate(op, Triple.class, KeyCount.class, page, pageSize,
                        operations.toArray(new AggregationOperation[operations.size()]));
    }

    private static final Random randomGenerator = new Random(System
            .currentTimeMillis());

    /**
     * Generate random unique array of 0 to max - 1
     *
     * @param count size of array
     * @param max   max of generated
     * @return
     */
    public static Set<Integer> randomIndex(int count, int max) {
        if (count >= max) {
            final Set<Integer> indexes = new HashSet<>(count);
            for (int i = 0; i < max; i++) indexes.add(i);
            return indexes;
        } else {
            final Set<Integer> indexes = new HashSet<>(count);
            while (indexes.size() < count)
                indexes.add(randomGenerator.nextInt(max));
            return indexes;
        }
    }
}
