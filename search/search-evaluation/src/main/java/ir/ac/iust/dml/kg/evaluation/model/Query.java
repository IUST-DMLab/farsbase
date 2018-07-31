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
public class Query {

   public static final String TYPE = "QUERY";
   // private Integer id;
    private String q;

   
    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }
    
 /*   public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
 */   
}
