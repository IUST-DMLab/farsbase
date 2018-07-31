/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.services.access.entities;

import org.springframework.data.annotation.Id;

public class UserAndCount {
  @Id
  private User key;
  private Long count;

  public User getKey() {
    return key;
  }

  public void setKey(User key) {
    this.key = key;
  }

  public Long getCount() {
    return count;
  }

  public void setCount(Long count) {
    this.count = count;
  }
}
