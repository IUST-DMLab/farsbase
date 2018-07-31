/*
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.search.feedback.logic.data

import java.nio.file.Path

data class ScanAddress(var address: String? = null, var depth: Int? = null, var path: Path? = null)
data class Config(
    var mapperExecutable: String? = null,
    var updaterLinkPath: String? = null,
    var runnerUrl: String? = null,
    var scanAddresses: MutableList<ScanAddress> = mutableListOf())