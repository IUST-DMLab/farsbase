/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.services.web.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class FilterRegistrationConfiguration {

  @Bean
  public FilterRegistrationBean someFilterRegistration() {
    FilterRegistrationBean registration = new FilterRegistrationBean();
    registration.setFilter(new SimpleCORSFilter());
    registration.addUrlPatterns("/*");
    registration.setName("corsFilter");
    registration.setOrder(1);
    return registration;
  }
}
