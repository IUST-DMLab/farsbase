/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.iust.dml.kg.evaluation.repo;

import ir.ac.iust.dml.kg.evaluation.model.UserResponse;
import java.util.List;


/**
 *
 * @author r.farjamfard
 */
public interface UserResponseRepo {
    
    void addUserResponse(UserResponse userResponse);
    UserResponse getUserResponseByPersonQuery(String query, String personId);
    List<UserResponse> getUserResponseByPersonId(String personId);
    List<UserResponse> getUserResponseByQuery(String q);
    void updateUserResponse(UserResponse userResponse);
    void deleteUserResponseByQuery(String query);
    void deleteUserResponseByPersonId(String personId);
    void deleteUserResponseByPersonQuery(String query, String personId);
    
 
}
