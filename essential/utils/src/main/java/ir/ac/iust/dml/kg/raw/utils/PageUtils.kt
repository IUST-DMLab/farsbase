/*
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.utils

object PageUtils {
  fun <T> asPages(page: Int, pageSize: Int, list: List<T>): PagedData<T> {
    val startIndex = page * pageSize
    val data =
        if (list.size < startIndex) mutableListOf()
        else {
          val endIndex = startIndex + pageSize
          list.subList(startIndex, if (list.size < endIndex) list.size else endIndex).toMutableList()
        }
    val totalSize = list.size.toLong()
    val pageCount = (totalSize / pageSize) + (if (totalSize % pageSize == 0L) 0 else 1)
    return PagedData(data, page, pageSize, pageCount, totalSize)
  }
}