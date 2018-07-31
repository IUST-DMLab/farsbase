package ir.ac.iust.dml.kg.raw.utils

import java.net.MalformedURLException
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.util.*

@Suppress("MemberVisibilityCanPrivate", "unused")
object UriChecker {

  val checkedUris = mutableMapOf<String, Boolean>()

  fun cachedCheckUri(uri: String) = checkedUris.getOrPut(uri, { checkUri(uri) })

  fun checkUri(uri: String) =
      try {
        URL(uri)
        URI(uri)
        true
      } catch (e: MalformedURLException) {
        try {
          System.err.println("Has not valid url $uri")
        } catch (ignored: UnknownFormatConversionException) {
        }
        false
      } catch (e: URISyntaxException) {
        try {
          System.err.println("Has not valid uri $uri")
        } catch (ignored: UnknownFormatConversionException) {
        }
        false
      }
}