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
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "corpus")
public class Occurrence {
  @Id
  @JsonIgnore
  private ObjectId uid;
  @Indexed
  private int occurrence;
  private String normalized;
  @JsonIgnore
  private List<String> words;
  @Field("postag")
  @JsonIgnore
  private List<String> posTags;
  @Field("generalized_sentence")
  private String generalizedSentence;
  private String raw;
  private String object;
  @Field("subject_type")
  @JsonIgnore
  private String subjectType;
  @Field("object_type")
  @JsonIgnore
  private String objectType;
  @Indexed
  private String predicate;
  private String subject;
  @Indexed
  private Boolean approved;
  @DBRef
  @Indexed
  private User assignee;
  @Indexed
  private String depTreeHash;
  @DBRef
  @Indexed
  private User selectedByUser;

  public ObjectId getUid() {
    return uid;
  }

  public void setUid(ObjectId uid) {
    this.uid = uid;
  }

  public String getId() {
    if (uid == null) return null;
    return uid.toHexString();
  }

  public int getOccurrence() {
    return occurrence;
  }

  public void setOccurrence(int occurrence) {
    this.occurrence = occurrence;
  }

  public String getNormalized() {
    return normalized;
  }

  public void setNormalized(String normalized) {
    this.normalized = normalized;
  }

  public List<String> getWords() {
    return words;
  }

  public void setWords(List<String> words) {
    this.words = words;
  }

  public List<String> getPosTags() {
    return posTags;
  }

  public void setPosTags(List<String> posTags) {
    this.posTags = posTags;
  }

  public String getGeneralizedSentence() {
    return generalizedSentence;
  }

  public void setGeneralizedSentence(String generalizedSentence) {
    this.generalizedSentence = generalizedSentence;
  }

  public String getRaw() {
    return raw;
  }

  public void setRaw(String raw) {
    this.raw = raw;
  }

  public String getObject() {
    return object;
  }

  public void setObject(String object) {
    this.object = object;
  }

  public String getSubjectType() {
    return subjectType;
  }

  public void setSubjectType(String subjectType) {
    this.subjectType = subjectType;
  }

  public String getObjectType() {
    return objectType;
  }

  public void setObjectType(String objectType) {
    this.objectType = objectType;
  }

  public String getPredicate() {
    return predicate;
  }

  public void setPredicate(String predicate) {
    this.predicate = predicate;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public Boolean getApproved() {
    return approved;
  }

  public void setApproved(Boolean approved) {
    this.approved = approved;
  }

  public User getAssignee() {
    return assignee;
  }

  public void setAssignee(User assignee) {
    this.assignee = assignee;
  }

  public String getDepTreeHash() {
    return depTreeHash;
  }

  public void setDepTreeHash(String depTreeHash) {
    this.depTreeHash = depTreeHash;
  }

  public User getSelectedByUser() {
    return selectedByUser;
  }

  public void setSelectedByUser(User selectedByUser) {
    this.selectedByUser = selectedByUser;
  }
}
