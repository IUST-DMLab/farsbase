/*
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.utils.dump.wiki

import org.slf4j.LoggerFactory
import java.io.*
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import javax.xml.bind.JAXBContext


class WikiDumpReader(path: Path) : Iterator<WikiArticle>, Closeable {

  val logger = LoggerFactory.getLogger(this.javaClass)!!
  var jaxbUnmarshaller = JAXBContext.newInstance(WikiArticle::class.java).createUnmarshaller()!!
  val reader = BufferedReader(InputStreamReader(FileInputStream(path.toFile()), "UTF8"))
  var lastArticle: WikiArticle? = null

  init {
    lastArticle = fetchNextArticle()
  }

  override fun close() {
    try {
      reader.close()
    } catch (e: Throwable) {
      logger.error("ir couldn't close wiki dump file", e)
    }
  }

  private fun fetchNextArticle(): WikiArticle? {
    val buffer = StringBuffer()
    var started = false
    while (true) {
      val line = reader.readLine() ?: break
      if (!started && line.trim().startsWith("<page>")) started = true
      if (started) buffer.append(line)
      if (started && line.endsWith("</page>"))
        return jaxbUnmarshaller.unmarshal(ByteArrayInputStream(
            buffer.toString().toByteArray(StandardCharsets.UTF_8))) as WikiArticle
    }
    return null
  }

  override fun hasNext(): Boolean {
    return lastArticle != null;
  }

  override fun next(): WikiArticle {
    val oldArticle = lastArticle!!
    lastArticle = fetchNextArticle()
    return oldArticle
  }
}