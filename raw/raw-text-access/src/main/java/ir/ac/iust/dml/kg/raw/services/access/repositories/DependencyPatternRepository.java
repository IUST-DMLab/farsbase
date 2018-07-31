/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.services.access.repositories;

import ir.ac.iust.dml.kg.raw.services.access.entities.DependencyPattern;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface DependencyPatternRepository
    extends MongoRepository<DependencyPattern, ObjectId>, DependencyPatternRepositoryCustom {
  @Query("{ pattern: {$eq : ?0} }")
  DependencyPattern findByPattern(String pattern);
}
