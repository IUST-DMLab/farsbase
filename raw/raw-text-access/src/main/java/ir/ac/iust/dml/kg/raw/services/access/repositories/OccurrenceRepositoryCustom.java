/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.services.access.repositories;

import ir.ac.iust.dml.kg.raw.services.access.entities.KeyAndCount;
import ir.ac.iust.dml.kg.raw.services.access.entities.Occurrence;
import ir.ac.iust.dml.kg.raw.services.access.entities.User;
import ir.ac.iust.dml.kg.raw.services.access.entities.UserAndCount;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * I am so lazy :-S I had never used MongoRepository before.
 * https://www.mkyong.com/spring-boot/spring-boot-spring-data-mongodb-example/
 */
public interface OccurrenceRepositoryCustom {

  Page<Occurrence> search(int page, int pageSize, String predicate, boolean like,
                          Integer minOccurrence, Boolean approved, Boolean assignee, User assigneeUser);

  Page<KeyAndCount> predicates(int page, int pageSize, String predicate);

  List<UserAndCount> assignees(String predicate);
}
