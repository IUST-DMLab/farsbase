/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.services.access.entities;

import ir.ac.iust.dml.kg.raw.extractor.ResolvedEntityToken;

import java.util.List;

public class ArticleSentence {
  private String sentence;
  private int numberOfRelations;
  private List<ResolvedEntityToken> tokens;

  public String getSentence() {
    return sentence;
  }

  public void setSentence(String sentence) {
    this.sentence = sentence;
  }

  public int getNumberOfRelations() {
    return numberOfRelations;
  }

  public void setNumberOfRelations(int numberOfRelations) {
    this.numberOfRelations = numberOfRelations;
  }

  public List<ResolvedEntityToken> getTokens() {
    return tokens;
  }

  public void setTokens(List<ResolvedEntityToken> tokens) {
    this.tokens = tokens;
  }
}
