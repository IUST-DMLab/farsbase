/*
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.utils.dump.triple

import com.google.gson.Gson
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.Closeable
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.file.Path

class TableJsonFileReader(path: Path) : Iterator<TableTripleData>, Closeable {

  val logger = LoggerFactory.getLogger(this.javaClass)!!
  val gson = Gson()
  val reader = BufferedReader(InputStreamReader(FileInputStream(path.toFile()), "UTF8"))
  var lastTriples: TableTripleData? = null

  init {
    lastTriples = fetchNextTriples()
  }

  private fun fetchNextTriples(): TableTripleData? {
    val buffer = StringBuffer()
    var started = false
    while (true) {
      val line = reader.readLine() ?: break
      if (line.trim().startsWith("{")) started = true
      else {
        if (started && line.contains("}")) {
          try {
            return gson.fromJson("{" + buffer.toString() + "}", TableTripleData::class.java)
          } catch (e: Throwable) {
            return TableTripleData()
          }
        } else if (started) buffer.append(line)
      }
    }
    return null
  }

  override fun close() {
    try {
      reader.close()
    } catch (e: Throwable) {
      logger.error("ir couldn't close triple dump data", e)
    }
  }

  override fun hasNext(): Boolean {
    return lastTriples != null
  }

  override fun next(): TableTripleData {
    val oldTriples = lastTriples!!
    lastTriples = fetchNextTriples()
    return oldTriples
  }
}