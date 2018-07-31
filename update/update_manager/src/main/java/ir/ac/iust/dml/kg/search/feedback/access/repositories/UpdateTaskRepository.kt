/*
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.search.feedback.access.repositories

import ir.ac.iust.dml.kg.search.feedback.access.entities.UpdateTask
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface UpdateTaskRepository : MongoRepository<UpdateTask, ObjectId>, UpdateTaskRepositoryCustom {
  @Query("{ path: {\$eq : ?0} }")
  fun findByPath(path: String): UpdateTask?
}