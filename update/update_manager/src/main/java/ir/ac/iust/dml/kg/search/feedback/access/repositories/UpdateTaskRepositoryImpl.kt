/*
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.search.feedback.access.repositories

import ir.ac.iust.dml.kg.search.feedback.access.entities.UpdateTask
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

@Suppress("Unused")
class UpdateTaskRepositoryImpl : UpdateTaskRepositoryCustom {

  @Autowired
  private val op: MongoTemplate? = null

  override fun search(page: Int, pageSize: Int, module: String?, path: String?,
                      minStartDate: Long?, maxStartDate: Long?,
                      minEndDate: Long?, maxEndDate: Long?): Page<UpdateTask> {
    val query = Query()
    if (module != null) query.addCriteria(Criteria.where("module").regex(module))
    if (path != null) query.addCriteria(Criteria.where("path").regex(path))
    if (minStartDate != null && maxStartDate != null)
      query.addCriteria(Criteria.where("startTime").gte(minStartDate).lte(maxStartDate))
    if (minStartDate != null && maxStartDate == null) query.addCriteria(Criteria.where("startTime").gte(minStartDate))
    if (minStartDate == null && maxStartDate != null) query.addCriteria(Criteria.where("startTime").lte(maxStartDate))
    if (minEndDate != null && maxEndDate != null)
      query.addCriteria(Criteria.where("endTime").gte(minEndDate).lte(maxEndDate))
    if (minEndDate != null && maxEndDate == null) query.addCriteria(Criteria.where("endTime").gte(minEndDate))
    if (minEndDate == null && maxEndDate != null) query.addCriteria(Criteria.where("endTime").lte(maxEndDate))
    query.with(Sort(Sort.Order(Sort.Direction.DESC, "startTime")))
    return SpringDataUtils.page(op, query, page, pageSize, UpdateTask::class.java)
  }
}
