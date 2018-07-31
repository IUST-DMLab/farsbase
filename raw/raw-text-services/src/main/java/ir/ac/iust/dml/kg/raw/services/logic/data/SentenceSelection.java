/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.services.logic.data;

import ir.ac.iust.dml.kg.raw.extractor.ResolvedEntityToken;

import java.util.List;

public class SentenceSelection {
  private List<ResolvedEntityToken> tokens;
  private String manualPredicate;
  private List<Integer> predicate;
  private List<Integer> subject;
  private List<Integer> object;

  public List<ResolvedEntityToken> getTokens() {
    return tokens;
  }

  public void setTokens(List<ResolvedEntityToken> tokens) {
    this.tokens = tokens;
  }

  public String getManualPredicate() {
    return manualPredicate;
  }

  public void setManualPredicate(String manualPredicate) {
    this.manualPredicate = manualPredicate;
  }

  public List<Integer> getPredicate() {
    return predicate;
  }

  public void setPredicate(List<Integer> predicate) {
    this.predicate = predicate;
  }

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
}
