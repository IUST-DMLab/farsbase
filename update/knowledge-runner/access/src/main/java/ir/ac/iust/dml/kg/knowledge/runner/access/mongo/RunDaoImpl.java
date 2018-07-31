package ir.ac.iust.dml.kg.knowledge.runner.access.mongo;

import ir.ac.iust.dml.kg.knowledge.commons.MongoDaoUtils;
import ir.ac.iust.dml.kg.knowledge.commons.PagingList;
import ir.ac.iust.dml.kg.knowledge.runner.access.dao.IHistoryDao;
import ir.ac.iust.dml.kg.knowledge.runner.access.dao.IRunDao;
import ir.ac.iust.dml.kg.knowledge.runner.access.entities.Run;
import ir.ac.iust.dml.kg.knowledge.runner.access.entities.RunState;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 */
@Repository
public class RunDaoImpl implements IRunDao {
    private final IHistoryDao historyDao;
    private final MongoOperations op;

    @Autowired
    public RunDaoImpl(IHistoryDao historyDao, MongoOperations op) {
        this.historyDao = historyDao;
        this.op = op;
    }

    @Override
    public void write(Run... runs) {
        for (Run j : runs) op.save(j);
    }

    @Override
    public void delete(Run... runs) {
        for (Run j : runs) {
            op.remove(j);
            try {
                historyDao.delete(j);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public Run read(ObjectId id) {
        return op.findOne(
                new Query().addCriteria(Criteria.where("id").is(id)),
                Run.class
        );
    }

    @Override
    public List<Run> readAllNeedForRerun() {
        final long epoch = System.currentTimeMillis();
        final Query query = new Query();
        query.addCriteria(Criteria.where("state").ne(RunState.Succeed).andOperator(
                new Criteria().orOperator(
                        Criteria.where("validUntilEpoch").exists(false),
                        Criteria.where("validUntilEpoch").gt(epoch)),
                new Criteria().orOperator(
                        Criteria.where("remindedTryCount").exists(false),
                        Criteria.where("remindedTryCount").gt(0)))
        );
        return op.find(query, Run.class);
    }

    @Override
    public PagingList<Run> search(String title, RunState state, int page, int pageSize) {
        final Query query = new Query();
        if (title != null)
            query.addCriteria(Criteria.where("title").regex(title));
        if (state != null)
            query.addCriteria(Criteria.where("state").is(state));
        return MongoDaoUtils.paging(op, Run.class, query, page, pageSize);
    }
}
