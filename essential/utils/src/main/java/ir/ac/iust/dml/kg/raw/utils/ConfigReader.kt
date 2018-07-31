/*
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by Majid Asgari.
 */

@file:Suppress("unused")

package ir.ac.iust.dml.kg.raw.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import nu.studer.java.util.OrderedProperties
import org.slf4j.LoggerFactory
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.reflect.KClass

object ConfigReader {

  val logger = LoggerFactory.getLogger(this.javaClass)!!

  fun getPath(key: String, defaultValue: String) = getPath(getString(key, defaultValue))

  fun getString(key: String, defaultValue: String): String {
    val p = getConfig(mapOf(key to defaultValue))
    return p.getProperty(key)
  }

  fun getInt(key: String, defaultValue: String): Int {
    val p = getConfig(mapOf(key to defaultValue))
    return p.getProperty(key).toInt()
  }

  fun getLong(key: String, defaultValue: String): Long {
    val p = getConfig(mapOf(key to defaultValue))
    return p.getProperty(key).toLong()
  }

  fun getBoolean(key: String, defaultValue: String): Boolean {
    val p = getConfig(mapOf(key to defaultValue))
    return p.getProperty(key).toBoolean()
  }

  fun getConfig(vararg keyValues: String): OrderedProperties {
    val map = mutableMapOf<String, String>()
    for (i in 0..keyValues.size / 2) map[keyValues[i]] = keyValues[i + 1]
    return getConfig(map)
  }

  @Suppress("MemberVisibilityCanPrivate")
  fun getConfig(keyValues: Map<String, Any>): OrderedProperties {
    val configPath =
        if (Files.exists(Paths.get("/srv/.pkg/config.properties")))
          Paths.get("/srv/.pkg/config.properties")
        else
          Paths.get(System.getProperty("user.home")).resolve(".pkg").resolve("config.properties")

    Files.createDirectories(configPath.parent)

    val config = OrderedProperties()

    if (!Files.exists(configPath)) logger.error("There is no file ${configPath.toAbsolutePath()} existed.")
    else
      FileInputStream(configPath.toFile()).use {
        try {
          config.load(it)
        } catch (e: Throwable) {
          logger.error("error in reading config file ${configPath.toAbsolutePath()}.", e)
        }
      }

    keyValues.forEach {
      try {
        config.getProperty(it.key)!!
      } catch (e: Throwable) {
        config.setProperty(it.key, it.value.toString())
      }
    }

    FileOutputStream(configPath.toFile()).use {
      config.store(it, null)
    }

    return config
  }

  fun getPath(string: String): Path {
    if (string.startsWith('/')) return Paths.get(string)
    var s =
        if (string.startsWith('~'))
          string.replace("~", System.getProperty("user.home")!!)
        else string
    if (s.contains("/")) return Paths.get(s)
    s = s.replace('/', File.separatorChar)
    return Paths.get(s)
  }

  fun <T : Any> readConfigObject(classPathSample: String, clazz: KClass<T>) =
      ConfigReader.readConfigObject(classPathSample, clazz.java)

  @Suppress("MemberVisibilityCanPrivate")
  fun <T> readConfigObject(classPathSample: String, clazz: Class<T>): T {
    val configPath =
        if (Files.exists(Paths.get("/srv/.pkg/$classPathSample")))
          Paths.get("/srv/.pkg/$classPathSample")
        else
          Paths.get(System.getProperty("user.home")).resolve(".pkg").resolve(classPathSample)
    if (!Files.exists(configPath))
      Files.copy(this::class.java.classLoader.getResourceAsStream(classPathSample), configPath)
    return readJson(configPath, clazz)
  }

  val gson = Gson()

  fun <T : Any> readJson(path: Path, clazz: KClass<T>)
      = gson.fromJson<T>(BufferedReader(InputStreamReader(FileInputStream(path.toFile()), "UTF-8")), clazz.java)!!

  @Suppress("MemberVisibilityCanPrivate")
  fun <T> readJson(path: Path, clazz: Class<T>)
      = gson.fromJson<T>(BufferedReader(InputStreamReader(FileInputStream(path.toFile()), "UTF-8")), clazz)!!

  @Suppress("UNCHECKED_CAST")
  fun <T> readListJson(path: Path): List<T> {
    val token = object : TypeToken<List<T>>() {}.type
    return gson.fromJson<List<T>>(BufferedReader(InputStreamReader(FileInputStream(path.toFile()), "UTF-8")),
        token)
  }

  @Suppress("UNCHECKED_CAST")
  fun <K, V> readMapJson(path: Path): Map<K, V> {
    val token = object : TypeToken<Map<K, V>>() {}.type
    return gson.fromJson<Map<K, V>>(BufferedReader(InputStreamReader(FileInputStream(path.toFile()), "UTF-8")),
        token)
  }
}