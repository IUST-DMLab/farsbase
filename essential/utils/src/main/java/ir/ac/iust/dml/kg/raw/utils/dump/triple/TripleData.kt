/*
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.utils.dump.triple

import com.google.gson.annotations.SerializedName

data class TripleData(
    var source: String? = null,
    var subject: String? = null,
    var predicate: String? = null,
    @SerializedName("template_name")
    var templateNameFull: String? = null,
    var templateName: String? = null,
    @SerializedName("template_type")
    var templateType: String? = null,
    @SerializedName("object")
    var objekt: String? = null
) {
  override fun toString(): String {
    return "($subject, $predicate, $objekt)"
  }
}