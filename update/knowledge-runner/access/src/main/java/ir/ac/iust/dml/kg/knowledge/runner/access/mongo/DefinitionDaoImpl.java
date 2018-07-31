package ir.ac.iust.dml.kg.knowledge.runner.access.mongo;

import ir.ac.iust.dml.kg.knowledge.runner.access.dao.IDefinitionDao;
import ir.ac.iust.dml.kg.knowledge.runner.access.entities.Definition;
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
public class DefinitionDaoImpl implements IDefinitionDao {
    @Autowired
    private MongoOperations op;

    @Override
    public void write(Definition... definitions) {
        for (Definition d : definitions) op.save(d);
    }

    @Override
    public void delete(Definition... definitions) {
        for (Definition d : definitions) op.remove(d);
    }

    @Override
    public Definition read(ObjectId id) {
        return op.findOne(
                new Query().addCriteria(Criteria.where("id").is(id)),
                Definition.class
        );
    }

    @Override
    public Definition readByTitle(String title) {
        return op.findOne(
                new Query().addCriteria(Criteria.where("title").is(title)),
                Definition.class
        );
    }

    @Override
    public List<Definition> readAll() {
        return op.findAll(Definition.class);
    }
}
