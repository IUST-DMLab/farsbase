/*
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.utils

object PropertyNormaller {
  val DIGIT_END_REGEX = Regex("([ابپتثجچحخدذرزژسشصضطظعغفقکگلمنوهی\\w: _]+)([۰۱۲۳۴۵۶۷۸۹\\d]+)")
  val DIGIT_END_REPLACE_REGEX = Regex("\\d*$")
  fun removeDigits(property: String, removeUnderscore: Boolean = true, removeSpaces: Boolean = false): String {
    var result =
        if (removeUnderscore) property.replace("_", " ")
        else if (removeSpaces) property.replace(" ", "_")
        else property
    if (DIGIT_END_REGEX.matches(result))
      result = result.replace(DIGIT_END_REPLACE_REGEX, "")
    return result.toLowerCase()
  }
}