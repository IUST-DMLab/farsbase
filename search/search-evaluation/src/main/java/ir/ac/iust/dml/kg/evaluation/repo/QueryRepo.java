/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.iust.dml.kg.evaluation.repo;

import ir.ac.iust.dml.kg.evaluation.model.Query;
import java.util.List;

/**
 *
 * @author r.farjamfard
 */
public interface QueryRepo {
    
    void addQuery(Query query);
   // Query getQueryById(Integer id);
   // void updateQuery(Query query);
    void deleteQuery(Query query);
    List<Query> getAllQuery();
}
