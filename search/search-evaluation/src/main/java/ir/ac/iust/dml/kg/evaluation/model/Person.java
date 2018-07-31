/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.iust.dml.kg.evaluation.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author r.farjamfard
 */
public class Person {
    
    public static final String TYPE = "PERSON";
    private Integer id;
    private String name;
   // private List<Integer> unreadQueryIds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

   /* public List<Integer> getUnreadQueryIds() {
        return unreadQueryIds;
    }

    public void setUnreadQueryIds(ArrayList<Integer> unreadQueryIds) {
        this.unreadQueryIds = unreadQueryIds;
    }
    
    public void insertQueryToUnreadList(Integer id){
        this.unreadQueryIds.add(id);
    }
   */
    
}
