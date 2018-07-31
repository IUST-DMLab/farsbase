/*
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.search.feedback.web.commons.filter

import java.io.IOException
import javax.servlet.*
import javax.servlet.http.HttpServletResponse

internal class SimpleCORSFilter : Filter {

  @Throws(IOException::class, ServletException::class)
  override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
    val httpServletResponse = response as HttpServletResponse
    httpServletResponse.setHeader("Access-Control-Allow-Origin", "*")
    httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true")
    httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE")
    httpServletResponse.setHeader("Access-Control-Max-Age", "3600")
    httpServletResponse.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept")
    chain.doFilter(request, response)
  }

  override fun destroy() {}

  override fun init(config: FilterConfig) {}
}