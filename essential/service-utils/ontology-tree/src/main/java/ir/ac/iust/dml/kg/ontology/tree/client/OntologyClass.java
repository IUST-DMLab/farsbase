package ir.ac.iust.dml.kg.ontology.tree.client;

public class OntologyClass {
  private String ontologyClass;
  private String parentOntologyClass;
  private String enLabel;
  private String comment;
  private String faLabel;
  private String faOtherLabels;
  private String note;
  private boolean approved;

  public String getOntologyClass() {
    return ontologyClass;
  }

  public void setOntologyClass(String ontologyClass) {
    this.ontologyClass = ontologyClass;
  }

  public String getParentOntologyClass() {
    return parentOntologyClass;
  }

  public void setParentOntologyClass(String parentOntologyClass) {
    this.parentOntologyClass = parentOntologyClass;
  }

  public String getEnLabel() {
    return enLabel;
  }

  public void setEnLabel(String enLabel) {
    this.enLabel = enLabel;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getFaLabel() {
    return faLabel;
  }

  public void setFaLabel(String faLabel) {
    this.faLabel = faLabel;
  }

  public String getFaOtherLabels() {
    return faOtherLabels;
  }

  public void setFaOtherLabels(String faOtherLabels) {
    this.faOtherLabels = faOtherLabels;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public boolean isApproved() {
    return approved;
  }

  public void setApproved(boolean approved) {
    this.approved = approved;
  }
}
