/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.iust.dml.kg.evaluation.service;

import ir.ac.iust.dml.kg.evaluation.model.Person;
import java.util.List;

/**
 *
 * @author r.farjamfard
 */
public interface PersonService {
    
    void savePerson(Person person);
    Person getPersonById(Integer id);
    void updatePerson(Person person);
    void deletePesron(Person person);
    void deletPersonById(Integer id);
    List<Person> getAllPerson();
}
