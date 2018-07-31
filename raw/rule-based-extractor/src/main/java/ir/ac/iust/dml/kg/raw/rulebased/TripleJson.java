/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Mohammad Abdous.
 */

package ir.ac.iust.dml.kg.raw.rulebased;

import org.joda.time.DateTime;

public class TripleJson {
  TripleJson() {
    this.template_name=null;
    this.template_type=null;
    this.version= DateTime.now().toDate().toString();
  }

  String object;
  String predicate;
  String source;
  String subject;
  String template_name;
  String template_type;
  String version;

  public String getObject() {
    return object;
  }

  public void setObject(String object) {
    this.object = object;
  }

  public String getPredicate() {
    return predicate;
  }

  public void setPredicate(String predicate) {
    this.predicate = predicate;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getTemplate_name() {
    return template_name;
  }

  public void setTemplate_name(String template_name) {
    this.template_name = template_name;
  }

  public String getTemplate_type() {
    return template_type;
  }

  public void setTemplate_type(String template_type) {
    this.template_type = template_type;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }
}
