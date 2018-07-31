/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.iust.dml.kg.evaluation.model;


import java.util.List;

/**
 *
 * @author r.farjamfard
 */
public class UserResponse {

    public static final String TYPE = "UserResponse";
    //private Person person;
    String personId;
    private Query query;
    private List<UserJudgment> judgmentList;
    private UserAnswerStatus status;

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public List<UserJudgment> getJudgmentList() {
        return judgmentList;
    }

    public void setJudgmentList(List<UserJudgment> judgmentList) {
        this.judgmentList = judgmentList;
    }

   
    public UserAnswerStatus getStatus() {
        return status;
    }

    public void setStatus(UserAnswerStatus status) {
        this.status = status;
    }

   /* public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
    */

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }
    
    
}
