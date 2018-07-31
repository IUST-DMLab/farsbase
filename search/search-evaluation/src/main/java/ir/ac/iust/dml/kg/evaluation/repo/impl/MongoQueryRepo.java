/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.iust.dml.kg.evaluation.repo.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import ir.ac.iust.dml.kg.evaluation.model.Query;
import ir.ac.iust.dml.kg.evaluation.repo.QueryRepo;
import ir.ac.iust.dml.kg.evaluation.util.MongoConnection;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author r.farjamfard
 */
public class MongoQueryRepo implements QueryRepo {

    private final MongoConnection mongoConnection;
    private final MongoClient mongoClient;
    private final DBCollection collection;

    public MongoQueryRepo(String dbName, String collectionName, String host, Integer port) {
        mongoConnection = new MongoConnection();
        this.mongoClient = mongoConnection.createSimpleConnection(host, port);
        collection = this.mongoClient.getDB(dbName).getCollection(collectionName);
    }

    public MongoQueryRepo(String dbName, String collectionName, String host, Integer port, String username, String password) {
        mongoConnection = new MongoConnection();
        this.mongoClient = mongoConnection.createConnection(username, dbName, password, host, port);
        collection = this.mongoClient.getDB(dbName).getCollection(collectionName);
    }

    @Override
    public void addQuery(Query query) {
        BasicDBObject document = new BasicDBObject();
        //  document.put("id", query.getId());
        document.put("query", query.getQ());
        document.put("type", query.TYPE);

        WriteResult result = this.collection.insert(document);

    }

    /*   @Override
     public Query getQueryById(Integer id) {
     BasicDBObject searchQuery = new BasicDBObject();
     searchQuery.put("type", Query.TYPE);
     searchQuery.put("id", id);
     DBCursor cursor = collection.find(searchQuery);
     BasicDBObject result = (BasicDBObject) cursor.next();
        
     Query query = new Query();
     query.setId(id);
     query.setQuery((String)result.get("query"));
       
        
     return query; 
     }
     */
    /*   @Override
     public void updateQuery(Query query) {
        
     Query originalQuery = getQueryById(query.getId());
     BasicDBObject originalDoc = new BasicDBObject();
     originalDoc.put("id", originalQuery.getId());
     originalDoc.put("query", originalQuery.getQ());
     originalDoc.put("type", Query.TYPE);
        
     BasicDBObject updatedDoc = new BasicDBObject();
     updatedDoc.put("id", query.getId());
     updatedDoc.put("query", query.getQ());
     updatedDoc.put("type", Query.TYPE);
     this.collection.update(originalDoc, updatedDoc);
     }*/

    /*  @Override
     public void deleteQueryById(Integer id) {
     Query query = getQueryById(id);
     BasicDBObject document = new BasicDBObject();
     document.put("id", query.getId());
     document.put("query", query.getQ());
     document.put("type", query.TYPE);
        
     this.collection.remove(document);
     }*/
    @Override
    public List<Query> getAllQuery() {
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("type", Query.TYPE);
        DBCursor cursor = this.collection.find(searchQuery);

        List<Query> queryList = new ArrayList<>();
        try {
            while (cursor.hasNext()) {
                DBObject result = cursor.next();
                Query query = new Query();
                //  query.setId((Integer) result.get("id"));
                query.setQ((String) result.get("query"));
       //     person.setUnreadQueryIds((ArrayList<Integer>) result.get("unreadQueryIds"));

                queryList.add(query);
            }
        } finally {
            cursor.close();
        }

        return queryList;
    }

    @Override
    public void deleteQuery(Query query) {
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("type", Query.TYPE);
        searchQuery.put("query", query.getQ());
        this.collection.remove(searchQuery);
    }

}
