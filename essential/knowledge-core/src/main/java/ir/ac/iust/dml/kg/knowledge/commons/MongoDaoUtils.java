package ir.ac.iust.dml.kg.knowledge.commons;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 * Utils for mongo query dao
 */
public class MongoDaoUtils {
    /**
     * Add paging to query
     *
     * @param op       mongo operations
     * @param clazz    type of entity to be read
     * @param query    query must executed
     * @param page     page to be read
     * @param pageSize size of each page
     * @param <T>
     * @return Paged data list
     */
    @SuppressWarnings("unchecked")
    public static <T> PagingList<T> paging(MongoOperations op, Class<T> clazz, Query query, int page, int pageSize) {
        if (pageSize > 0) {
            query.with(new PageRequest(page, pageSize));
            final List<T> list = op.find(query, clazz);
            final long total = op.count(query, clazz);
            return new PagingList(list, page, pageSize, total);
        } else {
            return new PagingList<T>(op.find(query, clazz));
        }
    }

    /**
     * Add paging on aggregation class
     * @param op mongo operations
     * @param source entity that aggregation run on it
     * @param destination type of result
     * @param page page to be read
     * @param pageSize size of each page
     * @param operations list of aggregation operation
     * @param <T1>
     * @param <T2>
     * @return Page data list
     */
    public static <T1, T2> PagingList<T2> aggregate(MongoOperations op, Class<T1> source, Class<T2> destination,
                                                    int page, int pageSize,
                                                    AggregationOperation... operations) {
        if (pageSize > 0) {
            //Add Skip and Limit to aggregate
            final List<AggregationOperation> finalOperations = new ArrayList<>(operations.length + 2);
            Collections.addAll(finalOperations, operations);
            finalOperations.add(Aggregation.skip(((long) page) * pageSize));
            finalOperations.add(Aggregation.limit(pageSize));
            final Aggregation agg = Aggregation.newAggregation(finalOperations);
            final AggregationResults<T2> aggRes = op.aggregate(agg, source, destination);
            final List<T2> result = aggRes.getMappedResults();

            if (page == 0 && result.size() == 0)
                return new PagingList<>(result, page, pageSize, 0);
            //Make Count query
            final List<AggregationOperation> countOperations = new ArrayList<>(operations.length + 1);
            Collections.addAll(countOperations, operations);
            countOperations.add(Aggregation.group().count().as("_count"));
            final Aggregation aggCount = Aggregation.newAggregation(countOperations);
            final AggregationResults<Object> aggCountResult = op.aggregate(aggCount, source, Object.class);
            int count = (Integer) ((Map) (aggCountResult.getUniqueMappedResult()))//
                    .get("_count");
            return new PagingList<>(result, page, pageSize, count);
        } else {
            final Aggregation agg = Aggregation.newAggregation(operations);
            final AggregationResults<T2> aggRes = op.aggregate(agg, source, destination);
            final List<T2> result = aggRes.getMappedResults();
            return new PagingList<>(result);
        }
    }

}
