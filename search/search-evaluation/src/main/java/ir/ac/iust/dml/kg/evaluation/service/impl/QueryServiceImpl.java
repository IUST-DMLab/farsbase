/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.iust.dml.kg.evaluation.service.impl;

import ir.ac.iust.dml.kg.evaluation.model.Query;
import ir.ac.iust.dml.kg.evaluation.model.UserResponse;
import ir.ac.iust.dml.kg.evaluation.repo.QueryRepo;
import ir.ac.iust.dml.kg.evaluation.repo.impl.MongoQueryRepo;
import ir.ac.iust.dml.kg.evaluation.service.QueryService;
import ir.ac.iust.dml.kg.evaluation.service.UserResponseService;
import java.util.List;

/**
 *
 * @author r.farjamfard
 */
public class QueryServiceImpl implements QueryService {

    private final QueryRepo queryRepo;
    private final UserResponseService userResponseService;

    public QueryServiceImpl(QueryRepo queryRepo, UserResponseService userResponseService) {
        this.queryRepo = queryRepo;
        this.userResponseService = userResponseService;
    }

    @Override
    public void saveQuery(Query query) {
        this.queryRepo.addQuery(query);

    }

    /*  @Override
     public Query getQueryById(Integer id) {
     return this.queryRepo.getQueryById(id);
     }
     */
    @Override
    public Query getUnreadQueryByPersonId(String personId) {
        List<Query> allQueries = queryRepo.getAllQuery();
        List<UserResponse> userResponseList = userResponseService.getUserResponseByPersonId(personId);

        for (Query query : allQueries) {
            if (isQueryResponded(query, userResponseList) == false) {
                //limit response count for each query
                List<UserResponse> queryUserRespones = userResponseService.getJudgedUserResponseByQuery(query.getQ());
                //if no other judgment or less than 3
                if (queryUserRespones == null || queryUserRespones.size() < 3) {
                    return query;
                }
            }
        }
        //no Unread query
        return null;
    }

    private boolean isQueryResponded(Query query, List<UserResponse> userResponseList) {
        if (userResponseList != null) {
            for (UserResponse response : userResponseList) {
                if (query.getQ().equalsIgnoreCase(response.getQuery().getQ())) {
                    return true;
                }
            }
        }
        return false;
    }

    /*  @Override
     public void updateQuery(Query query) {
     this.queryRepo.updateQuery(query);
     }

     @Override
     public void deleteQueryById(Integer id) {
     this.queryRepo.deleteQueryById(id);
     }*/
    @Override
    public List<Query> getAllQueries() {
        return this.queryRepo.getAllQuery();
    }

    @Override
    public void deleteQuery(Query query) {
        this.userResponseService.deleteUserResponseByQuery(query.getQ());
        this.queryRepo.deleteQuery(query);
    }

}
