/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.iust.dml.kg.evaluation.service.impl;

import ir.ac.iust.dml.kg.evaluation.model.Person;
import ir.ac.iust.dml.kg.evaluation.repo.PersonRepo;
import ir.ac.iust.dml.kg.evaluation.service.PersonService;
import java.util.List;

/**
 *
 * @author r.farjamfard
 */
public class PersonServiceImpl implements PersonService{
    
    private final PersonRepo personRepo;
  
    
    public PersonServiceImpl(PersonRepo personRepo){
        this.personRepo = personRepo;
    }

    @Override
    public void savePerson(Person person) {
        
        this.personRepo.addPeson(person);
    }

    @Override
    public Person getPersonById(Integer id) {
        Person person = this.personRepo.getPersonById(id);
        return person;
    }

    @Override
    public void updatePerson(Person person) {
        this.personRepo.updatePerson(person);
    }

    @Override
    public void deletePesron(Person person) {
        this.personRepo.deletePesron(person);
    }

    @Override
    public void deletPersonById(Integer id) {
        this.personRepo.deletePersonById(id);
    }

    @Override
    public List<Person> getAllPerson() {
        List<Person> personList = this.personRepo.getAllPerson();
        return personList;
    }
    
}
