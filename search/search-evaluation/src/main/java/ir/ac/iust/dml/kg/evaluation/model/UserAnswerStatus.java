/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.iust.dml.kg.evaluation.model;

/**
 *
 * @author r.farjamfard
 */
public class UserAnswerStatus {
    
    private Boolean ableToAnswer;
    private String reason;

    public Boolean isAbleToAnswer() {
        return ableToAnswer;
    }

    public void setAbleToAnswer(Boolean ableToAnswer) {
        this.ableToAnswer = ableToAnswer;
    }

    public String getReson() {
        return reason;
    }

    public void setReson(String reason) {
        this.reason = reason;
    }
    
}
