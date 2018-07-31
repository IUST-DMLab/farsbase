/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.iust.dml.kg.evaluation.service.impl;


import ir.ac.iust.dml.kg.evaluation.model.UserResponse;
import ir.ac.iust.dml.kg.evaluation.repo.UserResponseRepo;
import ir.ac.iust.dml.kg.evaluation.service.UserResponseService;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author r.farjamfard
 */
public class UserResponseServiceImpl implements UserResponseService{
    
    private final UserResponseRepo userResponseRepo;

    public UserResponseServiceImpl(UserResponseRepo userResponseRepo) {
        this.userResponseRepo = userResponseRepo;
    }
    
    

    @Override
    public void saveUserResponse(UserResponse userResponse) {
        if(userResponse.getStatus().isAbleToAnswer()){
            userResponse.getStatus().setReson(null);
        }
        if(!userResponse.getStatus().isAbleToAnswer())
        {
            userResponse.setJudgmentList(null);
        }
        this.userResponseRepo.addUserResponse(userResponse);
    }

    @Override
    public List<UserResponse> getUserResponseByPersonId(String personId) {
        return this.userResponseRepo.getUserResponseByPersonId(personId);
    }

    @Override
    public List<UserResponse> getUserResponseByQuery(String query) {
        return this.userResponseRepo.getUserResponseByQuery(query);
    }

    @Override
    public void deleteUserResponseByQuery(String query) {
        this.userResponseRepo.deleteUserResponseByQuery(query);
    }

    @Override
    public List<UserResponse> getJudgedUserResponseByQuery(String query) {
        List<UserResponse> allResponses=this.getUserResponseByQuery(query);
        List<UserResponse> judgedResponses=new ArrayList<>();
        for(UserResponse userResponse:allResponses)
        {
            if(userResponse.getStatus().isAbleToAnswer())
            {
                judgedResponses.add(userResponse);
            }
        }
        return judgedResponses;
    }

   
}
