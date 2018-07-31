/*
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.search.feedback.logic

import ir.ac.iust.dml.kg.raw.utils.ConfigReader
import ir.ac.iust.dml.kg.search.feedback.logic.data.Config
import ir.ac.iust.dml.kg.search.feedback.logic.data.ScanAddress
import org.springframework.stereotype.Service
import java.nio.file.Path
import java.nio.file.Paths

@Service
class UpdateConfigurator {
  val mapperPath: Path
  val wikiUpdateLinkedPath: Path
  val tablesUpdateLinkedPath: Path
  val rawUpdateLinkedPath: Path
  val scanAddresses: MutableList<ScanAddress>
  val runnerUrl: String

  init {
    val config = ConfigReader.readConfigObject("scan_config.json", Config::class.java)
    mapperPath = ConfigReader.getPath(config.mapperExecutable!!)
    val updateLinkPath = ConfigReader.getPath(config.updaterLinkPath!!)
    wikiUpdateLinkedPath = updateLinkPath.resolve("wiki")
    tablesUpdateLinkedPath = updateLinkPath.resolve("table")
    rawUpdateLinkedPath = updateLinkPath.resolve("raw")
    scanAddresses = config.scanAddresses
    scanAddresses.forEach { it.path = Paths.get(it.address!!) }
    runnerUrl = config.runnerUrl!!
  }
}