/*
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.search.feedback.logic

import ir.ac.iust.dml.kg.raw.utils.Module
import ir.ac.iust.dml.kg.services.client.runner.ApiClient
import ir.ac.iust.dml.kg.services.client.runner.swagger.V1definitionsApi
import ir.ac.iust.dml.kg.services.client.runner.swagger.V1runApi
import ir.ac.iust.dml.kg.services.client.runner.swagger.model.CommandLineData
import ir.ac.iust.dml.kg.services.client.runner.swagger.model.DefinitionData
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class UpdateRunner {

  private @Autowired lateinit var settings: UpdateConfigurator
  private val LOGGER = Logger.getLogger(this.javaClass)!!
  private lateinit var runApi: V1runApi
  private lateinit var runDefinitionApi: V1definitionsApi

  @PostConstruct
  fun createUrls() {
    val client = ApiClient()
    client.basePath = settings.runnerUrl
    client.connectTimeout = 4800000
    runApi = V1runApi(client)
    runDefinitionApi = V1definitionsApi(client)
    createRuns()
  }

  private fun createRuns() {
    val map = mutableMapOf<String, DefinitionData>()
    runDefinitionApi.all1().forEach { map[it.title] = it }
    val wikiDefinition = map.getOrDefault("wiki", DefinitionData())
    saveRun(wikiDefinition, "wiki", "-jar", "mapper.jar", "completeDump", "knowledgeStore",
        settings.wikiUpdateLinkedPath.toAbsolutePath().toString())
    val tableDefinition = map.getOrDefault("table", DefinitionData())
    saveRun(tableDefinition, "table", "-jar", "mapper.jar", "tables", "knowledgeStore",
        settings.tablesUpdateLinkedPath.toAbsolutePath().toString())
    val rawDefinition = map.getOrDefault("raw", DefinitionData())
    saveRun(rawDefinition, "raw", "-jar", "mapper.jar", "raw", "knowledgeStore",
        settings.rawUpdateLinkedPath.toAbsolutePath().toString())
  }

  private fun saveRun(definition: DefinitionData, title: String, vararg commands: String) {
    definition.title = title
    definition.maxTryCount = 5
    definition.maxTryDuration = 24 * 3600 * 1000L
    val commandListData = CommandLineData()
    commandListData.command = "java"
    commandListData.arguments.addAll(commands)
    commandListData.workingDirectory = settings.mapperPath.toAbsolutePath().toString()
    definition.commands = mutableListOf(commandListData)
    LOGGER.info("saving run ${definition.title} on runner service")
    runDefinitionApi.insert1(definition)
  }

  private fun run(title: String) =
      try {
        val ran = runApi.run1(title)
        ran?.identifier
      } catch (th: Throwable) {
        null
      }

  fun getRunPath(module: Module) =
      when (module) {
        Module.raw_mapper_entity_adder,
        Module.raw_rule_based,
        Module.raw_distant_supervision_logistic,
        Module.raw_distant_supervision_deep,
        Module.raw_dependency_pattern -> settings.rawUpdateLinkedPath
        Module.wiki -> settings.wikiUpdateLinkedPath
        Module.web_table_extractor -> settings.tablesUpdateLinkedPath
        Module.mapper_auto_labeling -> null
        Module.expert -> null
        Module.manual -> null
        Module.sameAs -> null
      }

  fun run(module: Module) =
      when (module) {
        Module.raw_mapper_entity_adder,
        Module.raw_rule_based,
        Module.raw_distant_supervision_logistic,
        Module.raw_distant_supervision_deep,
        Module.raw_dependency_pattern -> run("raw")
        Module.wiki -> run("wiki")
        Module.web_table_extractor -> run("table")
        Module.mapper_auto_labeling -> null
        Module.expert -> null
        Module.manual -> null
        Module.sameAs -> null
      }
}