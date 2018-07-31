/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.services.access.repositories;

import ir.ac.iust.dml.kg.raw.services.access.entities.Occurrence;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface OccurrenceRepository extends MongoRepository<Occurrence, ObjectId>, OccurrenceRepositoryCustom {
  @Query("{}")
  Page<Occurrence> findAll(Pageable pageable);

  @Query("{ occurrence: {$gte  : ?0} }")
  Page<Occurrence> findAll(int minOccurrence, Pageable pageable);

  Page<Occurrence> findByPredicateOrApproved(String predicate, Boolean approved, Pageable pageable);

  @Query("{ normalized: ?0 }")
  Occurrence getByNormalized(String sentence);
}
