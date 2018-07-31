/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.iust.dml.kg.evaluation.services.web.rest.factory;

import ir.ac.iust.dml.kg.evaluation.repo.QueryRepo;
import ir.ac.iust.dml.kg.evaluation.repo.UserResponseRepo;
import ir.ac.iust.dml.kg.evaluation.repo.impl.MongoQueryRepo;
import ir.ac.iust.dml.kg.evaluation.repo.impl.MongoUserResponseRepo;
import ir.ac.iust.dml.kg.evaluation.service.KnowledgeGraphEvaluator;
import ir.ac.iust.dml.kg.evaluation.service.QueryService;
import ir.ac.iust.dml.kg.evaluation.service.UserResponseService;
import ir.ac.iust.dml.kg.evaluation.service.impl.KnowledgeGraphEvaluatorImpl;
import ir.ac.iust.dml.kg.evaluation.service.impl.QueryServiceImpl;
import ir.ac.iust.dml.kg.evaluation.service.impl.UserResponseServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author morteza.khaleghi
 */
@Service
public class EvaluationServicesFactory {

    private UserResponseService userResponseService;
    private QueryService queryService;
    private KnowledgeGraphEvaluator knowledgeGraphEvaluator;

    @Value("${evaluation.db.dbName}")
    private String dbName;

    @Value("${evaluation.db.collection}")
    private String collection;

    @Value("${evaluation.db.host}")
    private String host;

    @Value("${evaluation.db.port}")
    private Integer port;

    @Value("${evaluation.db.userName}")
    private String userName;

    @Value("${evaluation.db.password}")
    private String password;

    public QueryService getQueryService() {
        if (this.queryService == null) {
            this.queryService = createQueryService();
        }
        return this.queryService;
    }

    public UserResponseService getUserResponseService() {
        if (this.userResponseService == null) {
            this.userResponseService = createUserReponseService();
        }
        return this.userResponseService;
    }
    
    public KnowledgeGraphEvaluator getKnowledgeGraphEvaluator()
    {
        if(this.knowledgeGraphEvaluator==null)
        {
            this.knowledgeGraphEvaluator=new KnowledgeGraphEvaluatorImpl(this.getUserResponseService());
        }
        return this.knowledgeGraphEvaluator;
    }

    private UserResponseService createUserReponseService() {
        UserResponseRepo userResponseRepo;
        if (dbHasUserNamePass()) {
            userResponseRepo = new MongoUserResponseRepo(dbName, collection, host, port, userName, password);
        } else {
            userResponseRepo = new MongoUserResponseRepo(dbName, collection, host, port);
        }
        UserResponseService userResponseServiceObj = new UserResponseServiceImpl(userResponseRepo);
        return userResponseServiceObj;
    }

    private QueryService createQueryService() {
        QueryRepo queryRepo;
        if (dbHasUserNamePass()) {
            queryRepo = new MongoQueryRepo(dbName, collection, host, port, userName, password);
        } else {
            queryRepo = new MongoQueryRepo(dbName, collection, host, port);
        }
        QueryService queryServiceObj = new QueryServiceImpl(queryRepo, this.userResponseService);
        return queryServiceObj;
    }

    private boolean dbHasUserNamePass() {
        if (this.userName != null && this.userName.trim().isEmpty() == false && this.password != null && this.password.trim().isEmpty() == false) {
            return true;
        } else {
            return false;
        }
    }

}
