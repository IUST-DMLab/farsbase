/*
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.search.feedback.access.repositories

import ir.ac.iust.dml.kg.search.feedback.access.entities.UpdateTask
import org.springframework.data.domain.Page

interface UpdateTaskRepositoryCustom {

  fun search(page: Int, pageSize: Int, module: String? = null, path: String? = null,
             minStartDate: Long?, maxStartDate: Long?,
             minEndDate: Long?, maxEndDate: Long?): Page<UpdateTask>
}