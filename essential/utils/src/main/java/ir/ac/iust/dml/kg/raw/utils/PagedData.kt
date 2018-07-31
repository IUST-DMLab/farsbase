/*
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.utils

data class PagedData<T>(var data: MutableList<T>,
                        var page: Int = 0, var pageSize: Int,
                        var pageCount: Long, var rowCount: Long)