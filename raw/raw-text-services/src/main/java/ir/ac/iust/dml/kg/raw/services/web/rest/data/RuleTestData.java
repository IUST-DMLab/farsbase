/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.services.web.rest.data;

import ir.ac.iust.dml.kg.raw.rulebased.RuleAndPredicate;

import java.util.List;

public class RuleTestData {
  private List<RuleAndPredicate> rules;
  private String text;

  public List<RuleAndPredicate> getRules() {
    return rules;
  }

  public void setRules(List<RuleAndPredicate> rules) {
    this.rules = rules;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
