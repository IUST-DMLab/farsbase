package ir.ac.iust.dml.kg.knowledge.store.access.mongo;

import ir.ac.iust.dml.kg.knowledge.store.access.dao.IVersionDao;
import ir.ac.iust.dml.kg.knowledge.store.access.entities.Version;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 * impl {@link IVersionDao}
 */
@Deprecated
@Repository
public class VersionDaoImpl implements IVersionDao {
    @Autowired
    @Qualifier("store1")
    private MongoOperations op;

    @Override
    public void write(Version... versions) {
        for (Version v : versions) {
            v.setModificationEpoch(System.currentTimeMillis());
            op.save(v);
        }
    }

    @Override
    public void delete(Version... versions) {
        for (Version v : versions) {
            op.remove(v);
        }
    }

    @Override
    public Version read(ObjectId id) {
        return op.findOne(
                new Query().addCriteria(Criteria.where("id").is(id)),
                Version.class
        );
    }

    @Override
    public Version readByModule(String module) {
        return op.findOne(
                new Query().addCriteria(Criteria.where("module").is(module)),
                Version.class
        );
    }

    @Override
    public List<Version> readAll() {
        return op.findAll(Version.class);
    }
}
