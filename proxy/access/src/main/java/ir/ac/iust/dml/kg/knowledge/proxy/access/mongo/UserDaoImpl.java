package ir.ac.iust.dml.kg.knowledge.proxy.access.mongo;

import ir.ac.iust.dml.kg.knowledge.commons.MongoDaoUtils;
import ir.ac.iust.dml.kg.knowledge.commons.PagingList;
import ir.ac.iust.dml.kg.knowledge.proxy.access.dao.IUserDao;
import ir.ac.iust.dml.kg.knowledge.proxy.access.entities.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 * <p>
 * impl {@link IUserDao}
 */
@Repository
public class UserDaoImpl implements IUserDao {
    @Autowired
    private MongoOperations op;

    @Override
    public void write(User... users) {
        for (User user : users)
            op.save(user);
    }

    @Override
    public void delete(User... users) {
        for (User user : users) {
            op.remove(user);
        }
    }

    @Override
    public User read(ObjectId id) {
        return op.findOne(
                new Query().addCriteria(Criteria.where("id").is(id)),
                User.class
        );
    }

    @Override
    public User readByUsername(String username) {
        return op.findOne(
                new Query().addCriteria(Criteria.where("username").is(username)),
                User.class
        );
    }

    @Override
    public PagingList<User> search(String name, String username, int page, int pageSize) {
        final Query query = new Query();
        if (name != null)
            query.addCriteria(Criteria.where("name").regex(name));
        if (username != null)
            query.addCriteria(Criteria.where("username").regex(username));
        return MongoDaoUtils.paging(op, User.class, query, page, pageSize);
    }
}
