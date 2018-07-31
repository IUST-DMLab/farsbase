/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.services.access.entities;

import java.util.List;

public class RelationDefinition {
  private List<Integer> subject;
  private List<Integer> object;
  private List<Integer> predicate;
  private String manualPredicate;
  private String mandatoryWord;
  private double accuracy;

  public List<Integer> getSubject() {
    return subject;
  }

  public void setSubject(List<Integer> subject) {
    this.subject = subject;
  }

  public List<Integer> getObject() {
    return object;
  }

  public void setObject(List<Integer> object) {
    this.object = object;
  }

  public List<Integer> getPredicate() {
    return predicate;
  }

  public void setPredicate(List<Integer> predicate) {
    this.predicate = predicate;
  }

  public double getAccuracy() {
    return accuracy;
  }

  public void setAccuracy(double accuracy) {
    this.accuracy = accuracy;
  }

  public String getManualPredicate() {
    return manualPredicate;
  }

  public void setManualPredicate(String manualPredicate) {
    this.manualPredicate = manualPredicate;
  }

  public String getMandatoryWord() {
    return mandatoryWord;
  }

  public void setMandatoryWord(String mandatoryWord) {
    this.mandatoryWord = mandatoryWord;
  }
}
