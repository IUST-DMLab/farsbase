/*
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.utils

object LanguageChecker {
  private val englishRegex = Regex("[\\w \\d'-:;.,]+")
  private val persianRegex = Regex("([۱۲۳۴۵۶۷۸۹۰؛،«»؟–ءيئؤًٌٍَُِّْةأإكٰٔٔآابپتثجچحخدذرزژسشصضطظعغفقکگلمنوهی\\u200C \\d'-:;.,]+.*)+")
  fun isEnglish(text: String) = !persianRegex.matches(text)
  fun isPersian(text: String) = persianRegex.matches(text)

  fun detectLanguage(text: String?) =
      if (text == null) null
      else {
        if (isEnglish(text)) "en" else "fa"
      }

  fun multiLanguages(text: String): Boolean {
    if (englishRegex.matches(text)) return false
    for (ch in text) if ((ch in 'A'..'Z') || (ch in 'a'..'z')) return true
    return false
  }
}