/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.services.split

import ir.ac.iust.dml.kg.raw.extractor.EnhancedEntityExtractor
import ir.ac.iust.dml.kg.raw.extractor.ResolvedEntityToken
import ir.ac.iust.dml.kg.raw.utils.ConfigReader
import ir.ac.iust.dml.kg.raw.utils.PathWalker
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path

object SplitLogic {

  private fun getSentences(repositoryPath: Path,
                           listener: (List<ResolvedEntityToken>) -> Unit) {
    val files = PathWalker.getPath(repositoryPath)
    files.forEach {
      println("reading file $it")
      try {
        EnhancedEntityExtractor.importFromFile(it).forEach(listener)
      } catch (th: Throwable) {
      }
    }
  }

  private fun getTokens(repositoryPath: Path,
                        listener: (ResolvedEntityToken) -> Unit) {
    getSentences(repositoryPath, { it.forEach(listener) })
  }

  fun findConjunctions(repositoryPath: Path) {
    val conjWords = mutableMapOf<String, Int>()
    getTokens(repositoryPath, {
      if (it.pos == "CONJ") {
        conjWords[it.word] = conjWords.getOrPut(it.word, { 0 }) + 1
      }
    })
    conjWords.toList().sortedByDescending { it.second }
        .forEach { println("Word $it is a conjunction") }
  }

  data class ConjunctionInfo(var conj: String,
                             var count: Int = 0,
                             var samples: MutableSet<List<ResolvedEntityToken>> = mutableSetOf())

  fun findConjunctionsAndDep(repositoryPath: Path) {
    val conjWords = mutableMapOf<String, ConjunctionInfo>()
    var totalNumberOfSentences = 0
    var totalNumberOfConjunctions = 0
    getSentences(repositoryPath, { sentence ->
      totalNumberOfSentences++
      sentence.forEach {
        if (it.pos == "CONJ") {
          totalNumberOfConjunctions++
          val out = sentence.filter { otherWord -> otherWord.dep?.head == it.dep.position }
              .map { it.dep.relation }.joinToString("@")
          val key = it.word + "#" + it.dep?.relation + "#" + out
          val info = conjWords.getOrPut(key, { ConjunctionInfo(it.word) })
          info.count++
          if (info.samples.size < 100) info.samples.add(sentence)
        }
      }
    })

    val outFolder = ConfigReader.getPath("out")
    if (!Files.exists(outFolder)) Files.createDirectories(outFolder)
    val MIN_SAMPLE = 10
    var numberOfWrappedSentences = 0
    PrintWriter(OutputStreamWriter(
        FileOutputStream(outFolder.resolve("stats.txt").toFile()),
        Charset.forName("UTF-8"))).use { out ->
      out.println("total conditions: ${conjWords.size}")
      out.println("total number of sentences: $totalNumberOfSentences")
      out.println("total number of conjunctions: $totalNumberOfConjunctions")
      conjWords.toList().sortedByDescending { it.second.count }
          .forEach {
            if (it.second.count > MIN_SAMPLE) numberOfWrappedSentences += it.second.count
            out.println("${it.second.count}\t${it.second.conj}\t${it.first}")
          }
      out.println("total number of wrapped sentences: $numberOfWrappedSentences")
    }

    conjWords.filter { it.value.count > MIN_SAMPLE }.forEach { key, (conj, count, samples) ->
      val conjFolder = outFolder.resolve(conj)
      if (!Files.exists(conjFolder)) Files.createDirectories(conjFolder)
      PrintWriter(OutputStreamWriter(
          FileOutputStream(conjFolder.resolve("$count-$key-samples.txt").toFile()),
          Charset.forName("UTF-8"))).use { out ->
        samples.forEach {
          out.println(it.joinToString(separator = " ", transform = { it.word }))
        }
      }
    }
  }
}