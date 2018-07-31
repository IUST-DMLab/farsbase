/*
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.search.feedback.logic

import ir.ac.iust.dml.kg.search.feedback.access.entities.UpdateTask
import ir.ac.iust.dml.kg.search.feedback.access.repositories.UpdateTaskRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

@Service
class UpdateRestLogic {

  @Autowired lateinit var repository: UpdateTaskRepository

  fun search(page: Int, pageSize: Int, module: String?, path: String?,
             minStartDate: Long?, maxStartDate: Long?,
             minEndDate: Long?, maxEndDate: Long?): Page<UpdateTask> {
    return repository.search(page, pageSize, module, path,
        minStartDate, maxStartDate, minEndDate, maxEndDate)
  }
}