package ir.ac.iust.dml.kg.knowledge.proxy.access.mongo;

import ir.ac.iust.dml.kg.knowledge.commons.MongoDaoUtils;
import ir.ac.iust.dml.kg.knowledge.commons.PagingList;
import ir.ac.iust.dml.kg.knowledge.proxy.access.dao.IPermissionDao;
import ir.ac.iust.dml.kg.knowledge.proxy.access.dao.IUserDao;
import ir.ac.iust.dml.kg.knowledge.proxy.access.entities.Permission;
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
public class PermissionDaoImpl implements IPermissionDao {
    @Autowired
    private MongoOperations op;

    @Override
    public void write(Permission... permissions) {
        for (Permission permission : permissions)
            op.save(permission);
    }

    @Override
    public void delete(Permission... permissions) {
        for (Permission permission : permissions) {
            op.remove(permission);
        }
    }

    @Override
    public Permission read(ObjectId id) {
        return op.findOne(
                new Query().addCriteria(Criteria.where("id").is(id)),
                Permission.class
        );
    }

    @Override
    public Permission readByTitle(String title) {
        return op.findOne(
                new Query().addCriteria(Criteria.where("title").is(title)),
                Permission.class
        );
    }

    @Override
    public List<Permission> readAll() {
        return op.findAll(Permission.class);
    }

    @Override
    public PagingList<Permission> search(String title, int page, int pageSize) {
        final Query query = new Query();
        if (title != null)
            query.addCriteria(Criteria.where("title").regex(title));
        return MongoDaoUtils.paging(op, Permission.class, query, page, pageSize);
    }
}
