/*
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.search.feedback.access.repositories

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query

object SpringDataUtils {
  fun <T> page(op: MongoTemplate?, query: Query, page: Int, pageSize: Int, entityClass: Class<T>): Page<T> {
    val total = op!!.count(query, entityClass)
    val p = PageRequest(page, pageSize)
    query.with(p)
    val list = op.find(query, entityClass)
    return PageImpl(list, p, total)
  }
}