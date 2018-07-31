/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.services.logic;

import ir.ac.iust.dml.kg.raw.services.access.entities.KeyAndCount;
import ir.ac.iust.dml.kg.raw.services.access.entities.Occurrence;
import ir.ac.iust.dml.kg.raw.services.access.entities.User;
import ir.ac.iust.dml.kg.raw.services.access.entities.UserAndCount;
import ir.ac.iust.dml.kg.raw.services.access.repositories.OccurrenceRepository;
import ir.ac.iust.dml.kg.raw.services.logic.data.AssigneeData;
import ir.ac.iust.dml.kg.raw.services.logic.data.OccurrenceSearchResult;
import ir.ac.iust.dml.kg.raw.services.logic.data.PredicateData;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OccurrenceLogic {
  @Autowired
  private OccurrenceRepository dao;
  @Autowired
  private UserLogic userLogic;

  public Occurrence findOne(String id) {
    return dao.findOne(new ObjectId(id));
  }

  public void save(Occurrence occurrence) {
    dao.save(occurrence);
  }

  public Page<Occurrence> export() {
    return dao.search(0, 1000000, null, false, null,
        true, null, null);
  }

  public Page<PredicateData> predicates(int page, int pageSize, String predicate, boolean fillAssignees) {
    final Page<KeyAndCount> predicates = dao.predicates(page, pageSize, predicate);
    final List<PredicateData> data = new ArrayList<>();
    predicates.getContent().forEach(p -> {
      final PredicateData d = new PredicateData(p.getKey(), p.getCount());
      if (fillAssignees) {
        d.setAssignees(new ArrayList<>());
        final List<UserAndCount> assignees = dao.assignees(p.getKey());
        assignees.forEach(a -> d.getAssignees().add(
            new AssigneeData(a.getKey() == null ? null : a.getKey().getUsername(), a.getCount())));
      }
      data.add(d);
    });
    return new PageImpl<>(data, new PageRequest(page, pageSize), predicates.getTotalPages());
  }

  public OccurrenceSearchResult search(String username, int page, int pageSize, String predicate, boolean like,
                                       Integer minOccurrence, Boolean approved, String assigneeUsername) {
    userLogic.getUserOrCreate(username);
    User assigneeUser = assigneeUsername == null ? null : userLogic.getUserOrCreate(assigneeUsername);
    final Page<Occurrence> p = dao.search(page, pageSize, predicate, like, minOccurrence,
        approved, null, assigneeUser);
    final long approvedCount;
    if (approved != null && approved) approvedCount = p.getTotalElements();
    else approvedCount = dao.search(0, 1, predicate, like, minOccurrence,
        true, null, assigneeUser).getTotalElements();

    final long rejectedCount;
    if (approved != null && !approved) rejectedCount = p.getTotalElements();
    else rejectedCount = dao.search(0, 1, predicate, like, minOccurrence,
        false, null, assigneeUser).getTotalElements();

    return new OccurrenceSearchResult(p, approvedCount, rejectedCount);
  }

  public List<AssigneeData> assigneeCount(String predicate) {
    final List<AssigneeData> result = new ArrayList<>();
    final List<UserAndCount> assignees = dao.assignees(predicate);
    assignees.forEach(a -> result.add(
        new AssigneeData(a.getKey() == null ? null : a.getKey().getUsername(), a.getCount())));
    return result;
  }
}
