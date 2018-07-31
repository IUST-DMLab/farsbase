/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.rulebased;

import edu.stanford.nlp.ling.tokensregex.TokenSequencePattern;

public class RuleAndPredicate {
  private String rule;
  private String predicate;
  private TokenSequencePattern pattern;

  public RuleAndPredicate() {
  }

  public RuleAndPredicate(String rule, String predicate) {
    this.rule = rule;
    this.predicate = predicate;
  }

  public String getRule() {
    return rule;
  }

  public void setRule(String rule) {
    this.rule = rule;
  }

  public String getPredicate() {
    return predicate;
  }

  public void setPredicate(String predicate) {
    this.predicate = predicate;
  }

  public TokenSequencePattern getPattern() {
    return pattern;
  }

  public void setPattern(TokenSequencePattern pattern) {
    this.pattern = pattern;
  }
}
