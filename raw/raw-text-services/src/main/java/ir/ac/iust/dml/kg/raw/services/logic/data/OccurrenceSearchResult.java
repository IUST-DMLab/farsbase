/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.services.logic.data;

import ir.ac.iust.dml.kg.raw.services.access.entities.Occurrence;
import org.springframework.data.domain.Page;

public class OccurrenceSearchResult {
  private Page<Occurrence> page;
  private Long numberOfApproved;
  private Long numberOfRejected;

  public OccurrenceSearchResult() {
  }

  public OccurrenceSearchResult(Page<Occurrence> page, Long numberOfApproved, Long numberOfRejected) {
    this.page = page;
    this.numberOfApproved = numberOfApproved;
    this.numberOfRejected = numberOfRejected;
  }

  public Page<Occurrence> getPage() {
    return page;
  }

  public void setPage(Page<Occurrence> page) {
    this.page = page;
  }

  public Long getNumberOfApproved() {
    return numberOfApproved;
  }

  public void setNumberOfApproved(Long numberOfApproved) {
    this.numberOfApproved = numberOfApproved;
  }

  public Long getNumberOfRejected() {
    return numberOfRejected;
  }

  public void setNumberOfRejected(Long numberOfRejected) {
    this.numberOfRejected = numberOfRejected;
  }
}
