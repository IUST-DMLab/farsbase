/*
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.search.feedback.web.commons.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableSwagger2
@EnableWebMvc
open class SwaggerConfig : WebMvcConfigurerAdapter() {
  @Bean
  open fun api(): Docket {
    return Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.regex(".*/rest/v1/.*"))
        .build().apiInfo(apiInfo())
  }

  fun apiInfo(): ApiInfo {
    val contact = Contact("دانشگاه علم و صنعت ایران",
        "dml.iust.ac.ir", "b_minaei@iust.ac.ir")
    return ApiInfo("سرور بروزرسانی",
        "منتشر شده در شهریور ۱۳۹۶",
        "0.1.0", null, contact, null, null)
  }

  override fun addResourceHandlers(registry: ResourceHandlerRegistry?) {
    registry!!.addResourceHandler("swagger-ui.html")
        .addResourceLocations("classpath:/META-INF/resources/")

    registry.addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/")
  }
}
