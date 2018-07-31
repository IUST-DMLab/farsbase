/*
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.search.feedback

import ir.ac.iust.dml.kg.search.feedback.web.commons.config.Jackson2ObjectMapperPrettier
import ir.ac.iust.dml.kg.search.feedback.web.commons.filter.FilterRegistrationConfiguration
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@EnableAutoConfiguration(exclude = arrayOf(Jackson2ObjectMapperPrettier::class,
    FilterRegistrationConfiguration::class))
@ComponentScan
open class Application

@Throws(Exception::class)
fun main(args: Array<String>) {
  SpringApplication.run(Application::class.java, *args)
}