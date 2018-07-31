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
import ir.ac.iust.dml.kg.evaluation.model.Person;
import ir.ac.iust.dml.kg.evaluation.repo.PersonRepo;
import ir.ac.iust.dml.kg.evaluation.util.MongoConnection;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author r.farjamfard
 */
public class MongoPersonRepo implements PersonRepo {

    private final MongoConnection mongoConnection;
    private final MongoClient mongoClient;
    private final DBCollection collection;

    public MongoPersonRepo(String dbName, String collectionName, String host, Integer port) {
        mongoConnection = new MongoConnection();
        this.mongoClient = mongoConnection.createSimpleConnection(host, port);
        collection = this.mongoClient.getDB(dbName).getCollection(collectionName);
    }
    
    public MongoPersonRepo(String dbName, String collectionName, String host, Integer port,String username,String password) {
        mongoConnection = new MongoConnection();
        this.mongoClient = mongoConnection.createConnection(username,dbName,password,host,port);
        collection = this.mongoClient.getDB(dbName).getCollection(collectionName);
    }

    @Override
    public void addPeson(Person person) {

        BasicDBObject document = new BasicDBObject();
        document.put("id", person.getId());
        document.put("name", person.getName());
        // document.put("unreadQueryIds", person.getUnreadQueryIds());
        document.put("type", Person.TYPE);

        WriteResult result = this.collection.insert(document);

    }

    @Override
    public Person getPersonById(Integer id) {
        BasicDBObject query = new BasicDBObject();
        query.put("type", Person.TYPE);
        query.put("id", id);
        DBCursor cursor = collection.find(query);

        BasicDBObject result = (BasicDBObject) cursor.next();
        Person person = new Person();
        person.setId(id);
        person.setName((String) result.get("name"));
        //  person.setUnreadQueryIds((ArrayList<Integer>) result.get("unreadQueryIds"));

        return person;
    }

    @Override
    public void updatePerson(Person person) {
        Person query = getPersonById(person.getId());
        BasicDBObject queryPerson = new BasicDBObject();
        queryPerson.put("id", query.getId());
        queryPerson.put("name", query.getName());
        //  queryPerson.put("unreadQueryIds", query.getUnreadQueryIds());
        queryPerson.put("type", Person.TYPE);

        BasicDBObject updatedPerson = new BasicDBObject();
        updatedPerson.put("id", person.getId());
        updatedPerson.put("name", person.getName());
        //  updatedPerson.put("unreadQueryIds", person.getUnreadQueryIds());
        updatedPerson.put("type", Person.TYPE);
        this.collection.update(queryPerson, updatedPerson);

    }

    @Override
    public void deletePesron(Person person) {
        BasicDBObject doc = new BasicDBObject();
        doc.put("id", person.getId());
        doc.put("name", person.getName());
        //  doc.put("unreadQueryIds", person.getUnreadQueryIds());
        doc.put("type", Person.TYPE);

        WriteResult result = collection.remove(doc);
        //int n = result.getN();

    }

    @Override
    public void deletePersonById(Integer id) {
        Person person = getPersonById(id);
        deletePesron(person);
    }

    @Override
    public List<Person> getAllPerson() {
        BasicDBObject query = new BasicDBObject();
        query.put("type", Person.TYPE);
        DBCursor cursor = this.collection.find(query);

        List<Person> personList = new ArrayList<>();
        try {
            while (cursor.hasNext()) {
                DBObject result = cursor.next();
                Person person = new Person();
                person.setId((Integer) result.get("id"));
                person.setName((String) result.get("name"));
                //     person.setUnreadQueryIds((ArrayList<Integer>) result.get("unreadQueryIds"));

                personList.add(person);
            }
        } finally {
            cursor.close();
        }

        return personList;

    }

}
