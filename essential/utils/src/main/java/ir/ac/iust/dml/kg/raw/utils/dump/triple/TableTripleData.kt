/*
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.utils.dump.triple

import com.google.gson.annotations.SerializedName

data class TableTripleData(
    @SerializedName("class")
    var ontologyClass: String? = null,
    var module: String? = null,
    @SerializedName("object")
    var objekt: String? = null,
    var predicate: String? = null,
    var source: String? = null,
    var subject: String? = null,
    var version: String? = null
)