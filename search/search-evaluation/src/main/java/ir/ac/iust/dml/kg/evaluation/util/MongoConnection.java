/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.iust.dml.kg.evaluation.util;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import java.util.ArrayList;

/**
 *
 * @author r.farjamfard
 */
public class MongoConnection {
    
    public MongoClient createConnection(String username, String dbName, String password, String host, int port){
        MongoCredential credential = MongoCredential.createCredential(username, dbName, password.toCharArray());
        ServerAddress serverAddress = new ServerAddress(host,port);
        ArrayList<MongoCredential> credentialsList = new ArrayList();
        credentialsList.add(credential);
        MongoClient mongoClient = new MongoClient(serverAddress, credentialsList);
        
        return mongoClient;
    }
    
    
     public MongoClient createSimpleConnection(String host, int port){
         MongoClient mongo = new MongoClient( host , port );
         return mongo;
     }
    
}
