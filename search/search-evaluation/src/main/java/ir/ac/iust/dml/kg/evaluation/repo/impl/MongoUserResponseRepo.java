/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.iust.dml.kg.evaluation.repo.impl;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import ir.ac.iust.dml.kg.evaluation.model.Query;
import ir.ac.iust.dml.kg.evaluation.model.UserAnswerStatus;
import ir.ac.iust.dml.kg.evaluation.model.UserJudgment;
import ir.ac.iust.dml.kg.evaluation.model.UserResponse;
import ir.ac.iust.dml.kg.evaluation.repo.UserResponseRepo;
import ir.ac.iust.dml.kg.evaluation.util.MongoConnection;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author r.farjamfard
 */
public class MongoUserResponseRepo implements UserResponseRepo {

    private final MongoConnection mongoConnection;
    private final MongoClient mongoClient;
    private final DBCollection collection;
    // private final PersonRepo personRepo;

    public MongoUserResponseRepo(String dbName, String collectionName, String host, Integer port) {
        mongoConnection = new MongoConnection();
        this.mongoClient = mongoConnection.createSimpleConnection(host, port);
        collection = this.mongoClient.getDB(dbName).getCollection(collectionName);
        // this.personRepo = personRepo;
    }

    public MongoUserResponseRepo(String dbName, String collectionName, String host, Integer port, String username, String password) {
        mongoConnection = new MongoConnection();
        this.mongoClient = mongoConnection.createConnection(username, dbName, password, host, port);
        collection = this.mongoClient.getDB(dbName).getCollection(collectionName);
        // this.personRepo = personRepo;
    }

    @Override
    public void addUserResponse(UserResponse userResponse) {
        BasicDBList dbList = new BasicDBList();
        BasicDBObject document = new BasicDBObject();
        if (userResponse.getJudgmentList() != null) {
            for (UserJudgment userJudgment : userResponse.getJudgmentList()) {
                BasicDBObject innerObject = new BasicDBObject();
                innerObject.put("answer", userJudgment.getAnswer());
                innerObject.put("relevant", userJudgment.isRelevant());
                dbList.add(innerObject);
            }
        }
        document.put("judgmentList", dbList);

        /* BasicDBObject personInnerObject = new BasicDBObject();
         personInnerObject.put("id", userResponse.getPerson().getId());
         personInnerObject.put("name", userResponse.getPerson().getName());
         document.put("person", personInnerObject);*/
        document.put("personId", userResponse.getPersonId());

        BasicDBObject queryInnerObject = new BasicDBObject();
        //  queryInnerObject.put("id", userResponse.getQuery().getId());
        queryInnerObject.put("query", userResponse.getQuery().getQ());
        document.put("query", queryInnerObject);

        BasicDBObject statusInnerObject = new BasicDBObject();
        statusInnerObject.put("ableToAnswer", userResponse.getStatus().isAbleToAnswer());
        statusInnerObject.put("reason", userResponse.getStatus().getReson());
        document.put("status", statusInnerObject);

        document.put("type", UserResponse.TYPE);

        WriteResult result = this.collection.insert(document);
    }

    @Override
    public List<UserResponse> getUserResponseByQuery(String q) {
        Query query = new Query();
        query.setQ(q);
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("type", UserResponse.TYPE);
        searchQuery.put("query.query", q);

        DBCursor cursor = collection.find(searchQuery);

        List<UserResponse> userResponseList = new ArrayList<>();

        try {
            while (cursor.hasNext()) {
                List<UserJudgment> userJudgmentList = new ArrayList<>();
                DBObject result = cursor.next();
                UserResponse userResponse = new UserResponse();

                BasicDBList dbList = (BasicDBList) result.get("judgmentList");
                for (Object judgmentObj : dbList) {
                    BasicDBObject judgmentBasicObj = (BasicDBObject) judgmentObj;
                    String answer = (String) judgmentBasicObj.get("answer");
                    Boolean relevant = (Boolean) judgmentBasicObj.get("relevant");
                    UserJudgment userJudgment = new UserJudgment();
                    userJudgment.setAnswer(answer);
                    userJudgment.setRelevant(relevant);
                    userJudgmentList.add(userJudgment);
                }
                userResponse.setJudgmentList(userJudgmentList);

                /*BasicDBObject personBasicObj = (BasicDBObject) result.get("person");
                 Person person = new Person();
                 person.setId((Integer) personBasicObj.get("id"));
                 person.setName((String) personBasicObj.get("name"));
                 userResponse.setPerson(person);*/
                userResponse.setPersonId((String) result.get("personId"));

                BasicDBObject queryBasicObj = (BasicDBObject) result.get("query");
                //  query.setId((Integer) queryBasicObj.get("id"));
                userResponse.setQuery(query);

                BasicDBObject statusBasicObj = (BasicDBObject) result.get("status");
                UserAnswerStatus status = new UserAnswerStatus();
                status.setAbleToAnswer((Boolean) statusBasicObj.get("ableToAnswer"));
                status.setReson((String) statusBasicObj.get("reason"));
                userResponse.setStatus(status);

                userResponseList.add(userResponse);
            }
        } finally {
            cursor.close();
        }

        return userResponseList;
    }

    @Override
    public List<UserResponse> getUserResponseByPersonId(String personId) {
       // Person person = this.personRepo.getPersonById(id);

        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("type", UserResponse.TYPE);
        searchQuery.put("personId", personId);

        DBCursor cursor = collection.find(searchQuery);

        List<UserResponse> userResponseList = new ArrayList<>();

        try {
            while (cursor.hasNext()) {
                List<UserJudgment> userJudgmentList = new ArrayList<>();
                DBObject result = cursor.next();
                UserResponse userResponse = new UserResponse();

                BasicDBList dbList = (BasicDBList) result.get("judgmentList");
                for (Object judgmentObj : dbList) {
                    BasicDBObject judgmentBasicObj = (BasicDBObject) judgmentObj;
                    String answer = (String) judgmentBasicObj.get("answer");
                    Boolean relevant = (Boolean) judgmentBasicObj.get("relevant");
                    UserJudgment userJudgment = new UserJudgment();
                    userJudgment.setAnswer(answer);
                    userJudgment.setRelevant(relevant);
                    userJudgmentList.add(userJudgment);
                }
                userResponse.setJudgmentList(userJudgmentList);

                //person
                //BasicDBObject personBasicObj = (BasicDBObject) result.get("person");
                //person.setId(id);
                //person.setName((String) personBasicObj.get("name"));
                // userResponse.setPerson(person);
                userResponse.setPersonId(personId);

                //query
                BasicDBObject queryBasicObj = (BasicDBObject) result.get("query");
                Query query = new Query();
                query.setQ((String) queryBasicObj.get("query"));
                //    query.setId((Integer) queryBasicObj.get("id"));
                userResponse.setQuery(query);

                //status
                BasicDBObject statusBasicObj = (BasicDBObject) result.get("status");
                UserAnswerStatus status = new UserAnswerStatus();
                status.setAbleToAnswer((Boolean) statusBasicObj.get("ableToAnswer"));
                status.setReson((String) statusBasicObj.get("reason"));
                userResponse.setStatus(status);

                userResponseList.add(userResponse);
            }
        } finally {
            cursor.close();
        }

        return userResponseList;
    }

    @Override
    public UserResponse getUserResponseByPersonQuery(String q, String personId) {

        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("type", UserResponse.TYPE);
        searchQuery.put("query.query", q);
        searchQuery.put("personId", personId);
        DBCursor cursor = collection.find(searchQuery);
        DBObject result = cursor.next();

        UserResponse userResponse = new UserResponse();
        List<UserJudgment> userJudgmentList = new ArrayList<>();

        BasicDBList dbList = (BasicDBList) result.get("judgmentList");
        for (Object judgmentObj : dbList) {
            BasicDBObject judgmentBasicObj = (BasicDBObject) judgmentObj;
            String answer = (String) judgmentBasicObj.get("answer");
            Boolean relevant = (Boolean) judgmentBasicObj.get("relevant");
            UserJudgment userJudgment = new UserJudgment();
            userJudgment.setAnswer(answer);
            userJudgment.setRelevant(relevant);
            userJudgmentList.add(userJudgment);
        }
        userResponse.setJudgmentList(userJudgmentList);

        //person
        /*Person person = personRepo.getPersonById(personId);
         userResponse.setPerson(person);*/
        userResponse.setPersonId(personId);

        //query
        BasicDBObject queryBasicObj = (BasicDBObject) result.get("query");
        Query query = new Query();
        query.setQ(q);
        //  query.setId((Integer) queryBasicObj.get("id"));
        userResponse.setQuery(query);

        //status
        BasicDBObject statusBasicObj = (BasicDBObject) result.get("status");
        UserAnswerStatus status = new UserAnswerStatus();
        status.setAbleToAnswer((Boolean) statusBasicObj.get("ableToAnswer"));
        status.setReson((String) statusBasicObj.get("reason"));
        userResponse.setStatus(status);

        return userResponse;
    }

    @Override
    public void updateUserResponse(UserResponse userResponse) {
        deleteUserResponseByPersonQuery(userResponse.getQuery().getQ(), userResponse.getPersonId());
        addUserResponse(userResponse);
    }

    @Override
    public void deleteUserResponseByQuery(String q) {
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("type", UserResponse.TYPE);
        searchQuery.put("query.query", q);
        this.collection.remove(searchQuery);

    }

    @Override
    public void deleteUserResponseByPersonId(String personId) {
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("type", UserResponse.TYPE);
        searchQuery.put("personId", personId);
        this.collection.remove(searchQuery);
    }

    @Override
    public void deleteUserResponseByPersonQuery(String query, String personId) {
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("type", UserResponse.TYPE);
        searchQuery.put("query.query", query);
        searchQuery.put("personId", personId);
        this.collection.remove(searchQuery);
    }

}
