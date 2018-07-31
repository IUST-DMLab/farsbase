package ir.ac.iust.dml.kg.knowledge.proxy.access.mongo;

import ir.ac.iust.dml.kg.knowledge.commons.MongoDaoUtils;
import ir.ac.iust.dml.kg.knowledge.commons.PagingList;
import ir.ac.iust.dml.kg.knowledge.proxy.access.dao.IForwardDao;
import ir.ac.iust.dml.kg.knowledge.proxy.access.dao.IUserDao;
import ir.ac.iust.dml.kg.knowledge.proxy.access.entities.Forward;
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
 * <p>
 * impl {@link IUserDao}
 */
@Repository
public class ForwardDaoImpl implements IForwardDao {
    @Autowired
    private MongoOperations op;

    @Override
    public void write(Forward... forwards) {
        for (Forward forward : forwards)
            op.save(forward);
    }

    @Override
    public void delete(Forward... forwards) {
        for (Forward forward : forwards) {
            op.remove(forward);
        }
    }

    @Override
    public Forward read(ObjectId id) {
        return op.findOne(
                new Query().addCriteria(Criteria.where("id").is(id)),
                Forward.class
        );
    }

    @Override
    public Forward readBySource(String source) {
        return op.findOne(
                new Query().addCriteria(Criteria.where("source").is(source)),
                Forward.class
        );
    }

    @Override
    public List<Forward> readAll() {
        return op.findAll(Forward.class);
    }

    @Override
    public PagingList<Forward> search(String source, int page, int pageSize) {
        final Query query = new Query();
        if (source != null)
            query.addCriteria(Criteria.where("source").regex(source));
        return MongoDaoUtils.paging(op, Forward.class, query, page, pageSize);
    }
}
