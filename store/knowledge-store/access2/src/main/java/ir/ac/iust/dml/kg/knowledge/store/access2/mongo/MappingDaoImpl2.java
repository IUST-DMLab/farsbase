package ir.ac.iust.dml.kg.knowledge.store.access2.mongo;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import ir.ac.iust.dml.kg.knowledge.commons.MongoDaoUtils;
import ir.ac.iust.dml.kg.knowledge.commons.PagingList;
import ir.ac.iust.dml.kg.knowledge.store.access2.dao.IMappingDao;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.PropertyMapping;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.TemplateMapping;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * impl {@link IMappingDao}
 */
@SuppressWarnings("Duplicates")
@Repository
public class MappingDaoImpl2 implements IMappingDao {
    private final MongoOperations op;

    @Autowired
    public MappingDaoImpl2(@Qualifier("store2") MongoOperations op) {
        this.op = op;
    }


    @Override
    public void write(TemplateMapping... mappings) {
        for (TemplateMapping m : mappings) {
            m.setModificationEpoch(System.currentTimeMillis());
            op.save(m);
        }
    }

    @Override
    public void delete(TemplateMapping... mappings) {
        for (TemplateMapping m : mappings)
            op.remove(m);
    }

    @Override
    public TemplateMapping read(ObjectId id) {
        return op.findOne(
                new Query().addCriteria(Criteria.where("id").is(id)),
                TemplateMapping.class
        );
    }

    @Override
    public TemplateMapping read(String template) {
        return op.findOne(
                new Query().addCriteria(Criteria.where("template").is(template)),
                TemplateMapping.class
        );
    }

    @Override
    public PagingList<TemplateMapping> readTemplate(Boolean hasTemplateMapping, Boolean hasPropertyMapping, int page, int pageSize) {
        final Query query = new Query();
        if (hasTemplateMapping != null)
            query.addCriteria(Criteria.where("rules").exists(hasTemplateMapping));
//        if (hasPropertyMapping != null)
//            query.addCriteria(Criteria.where("properties.rules").exists(hasPropertyMapping));
        return MongoDaoUtils.paging(op, TemplateMapping.class, query, page, pageSize);
    }

    @Override
    public PagingList<TemplateMapping> searchTemplate(String template, int page, int pageSize) {
        final Query query = new Query();
        if (template != null)
            query.addCriteria(Criteria.where("template").regex(template));
        query.with(new Sort(Sort.Direction.DESC, "weight"));
        return MongoDaoUtils.paging(op, TemplateMapping.class, query, page, pageSize);
    }

    @Override
    public PagingList<PropertyMapping> searchProperty(String template, String property, int page, int pageSize) {
        List<DBObject> pipeline = new ArrayList<>();
        if (template != null)
            pipeline.add(new BasicDBObject()
                    .append("$match", new BasicDBObject("template", new BasicDBObject().append("$regex", template))));
        pipeline.add(new BasicDBObject().append("$unwind", "$properties"));
        pipeline.add(new BasicDBObject().append("$replaceRoot", new BasicDBObject().append("newRoot", "$properties")));
        if (property != null)
            pipeline.add(new BasicDBObject()
                    .append("$match", new BasicDBObject("property", new BasicDBObject().append("$regex", property))));
        if (pageSize > 0) {
            pipeline.add(new BasicDBObject().append("$limit", pageSize));
            pipeline.add(new BasicDBObject().append("$skip", page * pageSize));
        }
        final Iterable<DBObject> result = op.getCollection("template-mapping").aggregate(pipeline).results();
        final ArrayList<PropertyMapping> convertedResult = new ArrayList<>();
        result.forEach(obj -> convertedResult.add(op.getConverter().read(PropertyMapping.class, obj)));

        if (pageSize <= 0)
            return new PagingList<>(convertedResult);
        pipeline.remove(pipeline.size() - 1);
        pipeline.remove(pipeline.size() - 1);
        pipeline.add(new BasicDBObject("$group", new BasicDBObject().append("_id", null).append("_count", new BasicDBObject("$sum", 1))));
        final Iterable<DBObject> aggCountResult = op.getCollection("template-mapping").aggregate(pipeline).results();
        int count = (int) ((DBObject) (((ArrayList) aggCountResult).get(0))).get("_count");
        return new PagingList<>(convertedResult, page, pageSize, count);
    }

    @Override
    public List<String> searchPredicate(String predicate, int max) {
        Aggregation ag = Aggregation.newAggregation(
                Aggregation.unwind("properties"),
                Aggregation.unwind("properties.rules"),
                Aggregation.group("properties.rules.predicate"),
                Aggregation.match(Criteria.where("_id").regex(predicate)),
                Aggregation.limit(max)
        );
        BasicDBList list = ((BasicDBList) (op.aggregate(ag, "template-mapping", String.class).getRawResults().get("result")));
        List<String> result = new ArrayList<>();
        list.forEach(
                a -> result.add(((BasicDBObject) a).get("_id").toString()));
        return result;
    }
}
