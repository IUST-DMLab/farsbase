/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.resource.extractor.client;

import java.util.HashSet;
import java.util.Set;

public class Resource {
  private String iri;
  private ResourceType type;
  private final Set<String> classTree = new HashSet<>();
  private final Set<String> variantLabel = new HashSet<>();
  private final Set<String> disambiguatedFrom = new HashSet<>();
  private String instanceOf;
  private String label;
  private double rank;

  public String getIri() {
    return iri;
  }

  public void setIri(String iri) {
    this.iri = iri;
  }

  public ResourceType getType() {
    return type;
  }

  public void setType(ResourceType type) {
    this.type = type;
  }

  public String getInstanceOf() {
    return instanceOf;
  }

  public void setInstanceOf(String instanceOf) {
    this.instanceOf = instanceOf;
  }

  public Set<String> getClassTree() {
    return classTree;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public Set<String> getVariantLabel() {
    return variantLabel;
  }

  public Set<String> getDisambiguatedFrom() {
    return disambiguatedFrom;
  }

  public double getRank() {
    return rank;
  }

  public void setRank(double rank) {
    this.rank = rank;
  }
}
