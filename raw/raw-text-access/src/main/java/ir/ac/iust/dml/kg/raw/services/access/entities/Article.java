/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.services.access.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "article")
public class Article {
  @Id
  @JsonIgnore
  private ObjectId uid;
  private String title;
  private String path;
  @Indexed
  private Integer numberOfSentences;
  @Indexed
  private Integer numberOfRelations;
  @Indexed
  private Float percentOfRelations;
  @Indexed
  private boolean approved = false;
  @Indexed
  @DBRef
  private User selectedByUser;

  private List<ArticleSentence> sentences = new ArrayList<>();

  public String getIdentifier() {
    return uid.toHexString();
  }

  public void setIdentifier(String identifier) {
    this.uid = new ObjectId(identifier);
  }

  public ObjectId getUid() {
    return uid;
  }

  public void setUid(ObjectId uid) {
    this.uid = uid;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public Integer getNumberOfSentences() {
    return numberOfSentences;
  }

  public void setNumberOfSentences(Integer numberOfSentences) {
    this.numberOfSentences = numberOfSentences;
  }

  public Integer getNumberOfRelations() {
    return numberOfRelations;
  }

  public void setNumberOfRelations(Integer numberOfRelations) {
    this.numberOfRelations = numberOfRelations;
  }

  public Float getPercentOfRelations() {
    return percentOfRelations;
  }

  public void setPercentOfRelations(Float percentOfRelations) {
    this.percentOfRelations = percentOfRelations;
  }

  public List<ArticleSentence> getSentences() {
    return sentences;
  }

  public void setSentences(List<ArticleSentence> sentences) {
    this.sentences = sentences;
  }

  public boolean isApproved() {
    return approved;
  }

  public void setApproved(boolean approved) {
    this.approved = approved;
  }

  public User getSelectedByUser() {
    return selectedByUser;
  }

  public void setSelectedByUser(User selectedByUser) {
    this.selectedByUser = selectedByUser;
  }
}
