/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.services.tree

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.stanford.nlp.ling.TaggedWord
import ir.ac.iust.dml.kg.raw.DependencyParser
import ir.ac.iust.dml.kg.raw.POSTagger
import ir.ac.iust.dml.kg.raw.SentenceTokenizer
import ir.ac.iust.dml.kg.raw.WordTokenizer
import ir.ac.iust.dml.kg.raw.extractor.ResolvedEntityToken
import ir.ac.iust.dml.kg.raw.services.access.entities.DependencyPattern
import ir.ac.iust.dml.kg.raw.services.access.entities.RelationDefinition
import ir.ac.iust.dml.kg.raw.services.access.repositories.DependencyPatternRepository
import ir.ac.iust.dml.kg.raw.services.access.repositories.OccurrenceRepository
import ir.ac.iust.dml.kg.raw.triple.RawTriple
import ir.ac.iust.dml.kg.raw.triple.RawTripleBuilder
import ir.ac.iust.dml.kg.raw.triple.RawTripleExporter
import ir.ac.iust.dml.kg.raw.triple.RawTripleExtractor
import ir.ac.iust.dml.kg.raw.utils.ConfigReader
import ir.ac.iust.dml.kg.raw.utils.Module
import ir.ac.iust.dml.kg.raw.utils.PathWalker
import ir.ac.iust.dml.kg.resource.extractor.client.ExtractorClient
import ir.ac.iust.dml.kg.resource.extractor.client.MatchedResource
import ir.ac.iust.dml.kg.resource.extractor.client.ResourceType
import ir.ac.iust.dml.kg.services.client.ApiClient
import ir.ac.iust.dml.kg.services.client.swagger.V1triplesApi
import org.apache.commons.logging.LogFactory
import org.bson.types.ObjectId
import org.maltparser.concurrent.graph.ConcurrentDependencyGraph
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.file.Files
import java.util.*

@Service
public class ParsingLogic : RawTripleExtractor {

  @Autowired lateinit private var dao: OccurrenceRepository
  @Autowired lateinit private var patternDao: DependencyPatternRepository
  private val logger = LogFactory.getLog(javaClass)
  private val DEP_CALC_ERROR = "error"
  private val extractor = ExtractorClient(ConfigReader.getString("resource.extractor.url", "http://localhost:8094"))
  val tripleApi: V1triplesApi

  init {
    val client = ApiClient()
    client.basePath = ConfigReader.getString("knowledge.store.url", "http://dmls.iust.ac.ir:8091/rs")
    client.connectTimeout = 1200000
    tripleApi = V1triplesApi(client)
  }

  fun findOne(id: String) = dao.findOne(ObjectId(id))

  fun searchPattern(page: Int, pageSize: Int,
                    reduceSize: Int? = 50, maxSentenceLength: Int?,
                    minSize: Int?, approved: Boolean?): Page<DependencyPattern>? {
    val result = patternDao.search(page, pageSize, maxSentenceLength, minSize, approved)
    if (reduceSize != null)
      result.forEach {
        if (it.samples.size > reduceSize) {
          val reducedSizeSet = mutableSetOf<String>()
          var i = 0
          for (x in it.samples) {
            if (i++ > reduceSize) break
            reducedSizeSet.add(x)
          }
          it.samples = reducedSizeSet
        }
      }
    return result
  }

  fun save(pattern: DependencyPattern): DependencyPattern {
    val oldPattern = patternDao.findByPattern(pattern.pattern)
    oldPattern.relations = pattern.relations
    return patternDao.save(oldPattern)
  }

  fun dependencyText(text: String) = convert(DependencyParser.parseRaw(text))

  fun dependencySentence(sentence: String) = convert(DependencyParser.parseRaw(sentence))[0]

  fun dependencySentences(sentences: List<String>) =
      convert(DependencyParser.parseRaw(sentences.joinToString(separator = " ")))

  fun convert(graphs: List<ConcurrentDependencyGraph>) = graphs.map { convert(it) }

  fun convert(graph: ConcurrentDependencyGraph): List<ParsedWord> {
    val words = mutableListOf<ParsedWord>()
    for (i in 1..graph.nTokenNodes()) {
      val node = graph.getDependencyNode(i)
      val parsedWord = ParsedWord()
      parsedWord.position = Integer.parseInt(node.getLabel("ID"))
      parsedWord.word = node.getLabel("FORM")
      parsedWord.lemma = node.getLabel("LEMMA")
      parsedWord.cPOS = node.getLabel("CPOSTAG")
      parsedWord.pos = node.getLabel("POSTAG")
      parsedWord.features = node.getLabel("FEATS")
      val headIdLabel = node.head.getLabel("ID")
      parsedWord.head = if (headIdLabel.isEmpty()) 0 else Integer.parseInt(headIdLabel)
      parsedWord.relation = node.getLabel("DEPREL")
      words.add(parsedWord)
    }
    return words
  }

  fun writeSizes() {
    var page = 0
    do {
      val pages = patternDao.findAll(PageRequest(page++, 1000))
      pages.forEach {
        it.count = it.samples.size
        it.sentenceLength = it.pattern.split("][").size
        patternDao.save(it)
      }
      logger.info("getting page $page form ${pages.totalPages}")
    } while (pages.hasNext())
  }

  fun writeParses() {
    val hashes = mutableMapOf<String, MutableSet<String>>()
    var uniqueSentenceCount = 0
    var page = 0
    val startTime = System.currentTimeMillis()
    do {
      val pages = dao.search(page++, 100, null, false, null, null, null, null)
      pages.filter { it.posTags != null && it.posTags.isNotEmpty() && it.posTags.size < 20 }.forEach {
        try {
          if (it.depTreeHash == null) {
            val posTags = mutableListOf<TaggedWord>()
            it.words.forEachIndexed { index, word -> posTags.add(TaggedWord(word, it.posTags[index])) }
            val depTree = DependencyParser.parse(posTags)
            if (depTree != null) it.depTreeHash = buildTreeHash(depTree)
            else it.depTreeHash = DEP_CALC_ERROR
            dao.save(it)
          }
          if (it.depTreeHash != DEP_CALC_ERROR) {
            val set = hashes.getOrPut(it.depTreeHash, { mutableSetOf() })
            if (!set.contains(it.raw)) {
              uniqueSentenceCount++
              set.add(it.raw)
            }
            if (uniqueSentenceCount % 1000 == 0)
              logger.info("${uniqueSentenceCount} >> ${hashes.size} ($page  of ${pages.totalPages}) " +
                  "in ${(System.currentTimeMillis() - startTime) / 1000} seconds")
          }
        } catch (th: Throwable) {
          logger.error(it.raw, th)
        }
      }
    } while (!pages.isLast)

//    hashes.filter { it.value.size > 1 }
//        .map { Triple(it.key, it.value, it.value.size) }
//        .sortedByDescending { it.third }
//        .forEach {
//          println("${it.third} - ${it.first}")
//          if(it.second.size < 10) it.second.forEach { sentence -> println(sentence) }
//        }

    var writtenPatterns = 0
    hashes.forEach { pattern, sentences ->
      writtenPatterns++
      val e = patternDao.findByPattern(pattern) ?: DependencyPattern(pattern)
      e.samples.addAll(sentences)
      e.count = e.samples.size
      e.sentenceLength = pattern.split("][").size
      patternDao.save(e)
      if (writtenPatterns % 1000 == 0)
        logger.info("$writtenPatterns of ${hashes.size} patterns has been written")
    }
  }

  private fun buildTreeHash(depTree: ConcurrentDependencyGraph): String {
    val hashBuilder = StringBuilder()
    for (index in 1..depTree.nTokenNodes()) {
      val token = depTree.getTokenNode(index)
      hashBuilder.append('[')
          .append(token.getLabel("POSTAG"))
          .append(',').append(token.headIndex)
          .append(',').append(token.getLabel("DEPREL"))
          .append(']')
    }
    return hashBuilder.toString()
  }

  private fun buildTreeHash(depTree: List<ResolvedEntityToken>): String {
    val hashBuilder = StringBuilder()
    depTree.forEach {
      hashBuilder.append('[')
          .append(it.pos)
          .append(',').append(it.dep.head)
          .append(',').append(it.dep.relation)
          .append(']')
    }
    return hashBuilder.toString()
  }

  fun writePatterns(removeOld: Boolean = false) {
    var page = 0
    val start = System.currentTimeMillis()
    do {
      val pages = patternDao.findAll(PageRequest(page++, 10))
      pages.forEach {
        try {
          if (calculatePatterns(it, removeOld)) patternDao.save(it)
        } catch (th: Throwable) {
          logger.error(th)
        }
      }
      logger.info("getting page $page form ${pages.totalPages}. " +
          "time passed: ${(System.currentTimeMillis() - start) / 1000} seconds")
    } while (pages.hasNext())
  }

  private data class MatchCount(var resource: MatchedResource, var count: Int)

  private val posTagMatcher = Regex("\\[(\\w+)??,")

  fun calculatePatterns(pattern: DependencyPattern, removeOld: Boolean): Boolean {
    if (removeOld && pattern.relations.isNotEmpty()) pattern.relations.clear()

    // sample pattern is: [AJe,5,SBJ][N,1,MOZ][Ne,5,MOS][AJ,3,NPOSTMOD][V,0,ROOT][PUNC,5,PUNC]
    // pos tags are list of AJe, N, Ne, AJ, V, PUNC
    val posTags = posTagMatcher.findAll(pattern.pattern!!).map { it.groupValues[1] }.toList()

    var checkedSamples = 0
    val countSet = mutableMapOf<Int, MatchCount>()
    val relationSet = mutableMapOf<Int, MatchCount>()
    pattern.samples.forEach { sample ->
      if (checkedSamples >= 10) return@forEach
      val entities = mutableListOf<MatchedResource>()
      val relations = mutableListOf<MatchedResource>()

      val words = WordTokenizer.tokenizeRaw(sample)[0].joinToString(" ")
      val matched = extractor.match(words, true)
      matched.forEach { mr ->
        if (mr.resource != null &&
            (mr.start != mr.end || posTags.size <= mr.start
                || !isBadMatchedResource(posTags[mr.start]))) {
          if (mr.resource.type != ResourceType.Property && mr.resource.instanceOf != null)
            entities.add(mr)
          else if (mr.resource.type == ResourceType.Property) relations.add(mr)
        }
      }

      if (entities.size >= 2) {
        entities.forEach {
          val key = it.start.hashCode() * 31 + it.end.hashCode()
          val c = countSet.getOrPut(key, { MatchCount(it, 0) })
          c.count++
        }
        if (relations.size == 1) {
          val relation = relations[0]
          val key = relation.start.hashCode() * 31 + relation.end.hashCode()
          val c = relationSet.getOrPut(key, { MatchCount(relation, 0) })
          c.count++
        }
      }
      checkedSamples++
    }

    if (countSet.size < 2) return false
    val minCount = checkedSamples * 0.8
    val topResources = countSet.filter { it.value.count > minCount }.toList()
    val resourcePair = mutableListOf<Pair<MatchCount, MatchCount>>()

    for (i in 0..topResources.size - 2)
      for (j in i + 1..topResources.size - 1) {
        val first = topResources[i].second
        val second = topResources[j].second
        if (first.resource.end < second.resource.start) resourcePair.add(Pair(first, second))
      }

    logger.info("found ${countSet.size} resources and ${relationSet.size} relations. " +
        "total ${resourcePair.size} resource pair is examining ...")
    resourcePair.forEach { pair ->
      val definition = RelationDefinition()
      definition.subject = addRange(pair.first.resource)
      definition.`object` = addRange(pair.second.resource)
      val oldDefinition = findOld(pattern.relations, definition)
      if (oldDefinition != null) pattern.relations.remove(oldDefinition)
      pattern.relations.add(definition)
      definition.accuracy = (pair.first.count + pair.second.count) / (2 * checkedSamples.toDouble())
      relationSet.forEach { relation ->
        logger.trace("finding a relation between ${pair.first.resource.resource.iri} and " +
            "${relation.value.resource.resource.iri} and ${pair.second.resource.resource.iri}")
        val search = tripleApi.search1(null, false,
            pair.first.resource.resource.iri, false,
            relation.value.resource.resource.iri, false,
            pair.second.resource.resource.iri, false, 0, 1)
        if (search.data.isNotEmpty()) {
          logger.info("found!! a relation between ${pair.first.resource.resource.iri} and " +
              "${relation.value.resource.resource.iri} and ${pair.second.resource.resource.iri}")
          definition.predicate = addRange(relation.value.resource)
          definition.accuracy = 1.0
        }
      }
    }

    return true
  }

  private fun key(relationDefinition: RelationDefinition): String {
    return relationDefinition.subject.map { it.toString() }.joinToString("-") + "/" +
        relationDefinition.`object`.map { it.toString() }.joinToString("-")
  }

  private fun findOld(relations: List<RelationDefinition>, newRelation: RelationDefinition): RelationDefinition? {
    val newKey = key(newRelation)
    relations.forEach { if (key(it) == newKey) return it }
    return null
  }

  private fun isBadMatchedResource(posTag: String) = posTag == "P" || posTag == "CONJ" || posTag == "V"

  fun addRange(resource: MatchedResource, list: MutableList<Int> = mutableListOf()): List<Int> {
    list += resource.start..resource.end
    return list
  }

  fun getWords(graph: ConcurrentDependencyGraph)
      = (1..graph.nTokenNodes()).map { graph.getDependencyNode(it).getLabel("FORM") }

  fun extractFromDb() {
    var page = 0
    val rtb = RawTripleBuilder(Module.raw_dependency_pattern.name, "http://dmls.iust.ac.ir/mongo/DistantSupervision",
        System.currentTimeMillis(), System.currentTimeMillis().toString(), true)
    val path = ConfigReader.getPath("raw.dependency.pattern.output.mongo", "~/raw/parsing/mongo.json")
    if (!Files.exists(path.parent)) Files.createDirectories(path.parent)
    var numberOfWrittenTriples = 0
    RawTripleExporter(path).use { exporter ->
      do {
        val data = patternDao.search(page++, 20, null, null, true)
        data.content.forEach { pattern ->
          pattern.samples.forEach { sample ->
            try {
              val sampleParts = WordTokenizer.tokenize(sample)
              val triple = rtb.create()
              pattern.relations.forEach relation@ { relation ->
                if (relation.mandatoryWord != null && !sampleParts.contains(relation.mandatoryWord)) return@relation
                val subject = getText(sampleParts, relation.subject) ?: return@relation
                val predicate =
                    if (relation.manualPredicate != null) relation.manualPredicate
                    else getText(sampleParts, relation.predicate) ?: return@relation
                val `object` = getText(sampleParts, relation.`object`) ?: return@relation
                triple.subject(subject).predicate(predicate).`object`(`object`)
                    .needsMapping(true).rawText(sample)
                exporter.write(triple)
                numberOfWrittenTriples++
                if (numberOfWrittenTriples % 100 == 0) logger.info("$numberOfWrittenTriples triples written to file")
              }
            } catch (th: Throwable) {
              logger.error(th)
            }
          }
        }
      } while (data.hasNext())
    }
  }

  fun getText(words: List<String>, indexes: List<Int>): String? {
    val builder = StringBuilder()
    indexes.forEach { builder.append(words[it]).append(' ') }
    if (builder.isEmpty()) return null
    builder.setLength(builder.length - 1)
    return builder.toString()
  }

  fun extractFromText() {
    val path = ConfigReader.getPath("raw.dependency.pattern.input.texts", "~/raw/parsing/texts")
    if (!Files.exists(path.parent)) Files.createDirectories(path.parent)
    if (!Files.exists(path)) {
      logger.error("folder ${path.toAbsolutePath()} is not existed.")
      return
    }
    val files = PathWalker.getPath(path, Regex("\\d+.json"))
    val type = object : TypeToken<Map<String, String>>() {}.type
    val gson = Gson()
    var numberOfWrittenTriples = 0
    var numberOfCheckedArticles = 0
    RawTripleExporter(ConfigReader.getPath("raw.dependency.pattern.output.raw", "~/raw/parsing/wikiText.json")).use { exporter ->
      files.forEach {
        val articleText: Map<String, String> = gson.fromJson(
            InputStreamReader(FileInputStream(it.toFile()), "UTF8"), type)
        articleText.values.forEach { text ->
          numberOfCheckedArticles++
          predict(object : TripleExtractionListener {
            override fun tripleExtracted(triple: RawTriple) {
              exporter.write(triple)
              numberOfWrittenTriples++
              logger.info("$numberOfWrittenTriples triples written to file" +
                  " (total $numberOfCheckedArticles articles checked.)\n. $triple")
            }
          }, "http://dmls.iust.ac.ir/raw/wiki", Date().toString(), text)
        }
      }
    }
  }

  override fun extract(source: String?, version: String?, text: String?): MutableList<RawTriple> {
    val triples = mutableListOf<RawTriple>()
    predict(object : TripleExtractionListener {
      override fun tripleExtracted(triple: RawTriple) {
        triple.accuracy = 0.9
        triples.add(triple)
      }
    }, source, version, text)
    return triples
  }

  override fun extract(source: String?, version: String?,
                       text: MutableList<MutableList<ResolvedEntityToken>>?): MutableList<RawTriple> {
    if (text == null || text.isEmpty()) return mutableListOf()
    val triples = mutableListOf<RawTriple>()
    val rtb = RawTripleBuilder(Module.raw_dependency_pattern.name, source ?: "http://fkg.iust.ac.ir/raw/unknown",
        System.currentTimeMillis(), version ?: System.currentTimeMillis().toString(), true)
    for (sentence in text) {
      try {
        val pattern = patternDao.findByPattern(buildTreeHash(sentence)) ?: continue
        pattern.relations.filter {
          it.manualPredicate != null || (it.predicate != null && it.predicate.isNotEmpty())
        }.forEach { relation ->
          if (relation.mandatoryWord != null && !sentence.map { it.word }.contains(relation.mandatoryWord))
            return@forEach
          val triple = rtb.create()
          triple.subject = relation.subject.map { sentence[it].word }.joinToString(" ")
          triple.predicate =
              if (relation.manualPredicate != null) relation.manualPredicate
              else relation.predicate.map { sentence[it].word }.joinToString(" ")
          triple.`object` = relation.`object`.map { sentence[it].word }.joinToString(" ")
          triple.rawText = sentence.joinToString { it.word }
          triple.accuracy = 0.9
          triples.add(triple)
        }
      } catch (e: Throwable) {
      }
    }
    return triples
  }

  interface TripleExtractionListener {
    fun tripleExtracted(triple: RawTriple)
  }

  private fun predict(listener: TripleExtractionListener,
                      source: String?, version: String?, text: String?) {
    if (text == null) return
    val rtb = RawTripleBuilder(Module.raw_dependency_pattern.name, source ?: "http://fkg.iust.ac.ir/raw/unknown",
        System.currentTimeMillis(), version ?: System.currentTimeMillis().toString(), true)
    val sentence = SentenceTokenizer.SentenceSplitterRaw(text)
    val taggedWords = sentence.map { POSTagger.tag(WordTokenizer.tokenize(it)) }
    val trees = DependencyParser.parseSentences(taggedWords)
    trees.forEachIndexed { index, tree ->
      try {
        val pattern = patternDao.findByPattern(buildTreeHash(tree)) ?: return@forEachIndexed
        val words = getWords(tree)
        pattern.relations.filter {
          it.manualPredicate != null || (it.predicate != null && it.predicate.isNotEmpty())
        }.forEach { relation ->
          if (relation.mandatoryWord != null && !words.contains(relation.mandatoryWord)) return@forEach
          val triple = rtb.create()
          triple.subject = relation.subject.map { words[it] }.joinToString(" ")
          triple.predicate =
              if (relation.manualPredicate != null) relation.manualPredicate
              else relation.predicate.map { words[it] }.joinToString(" ")
          triple.accuracy = 0.9
          triple.`object` = relation.`object`.map { words[it] }.joinToString(" ")
          triple.rawText = sentence[index]
          listener.tripleExtracted(triple)
        }
      } catch (e: Throwable) {
      }
    }
  }
}
