/*
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.search.feedback.logic.data

import ir.ac.iust.dml.kg.raw.utils.Module

data class UpdateInfo(
    var extractionStart: Long? = null,
    var extractionEnd: Long? = null,
    var module: Module? = null
)