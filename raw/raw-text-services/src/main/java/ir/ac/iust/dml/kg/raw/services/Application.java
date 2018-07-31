/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.services;

import ir.ac.iust.dml.kg.raw.services.web.filter.FilterRegistrationConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;
import java.util.Properties;
import java.util.Random;

@SpringBootApplication
//@ImportResource(value = {})
@EnableAutoConfiguration(exclude = {
    FilterRegistrationConfiguration.class})
@ComponentScan(value = "ir.ac.iust.dml.kg.raw")
public class Application {

  public static void main(String[] args) throws IOException {
    SpringApplication app = new SpringApplication(Application.class);
    Properties properties = new Properties();
    if (args.length > 0) properties.put("server.port", 10200 + (new Random().nextInt(1000)));
    else properties.put("server.port", 8100);
    app.setDefaultProperties(properties);
    ConfigurableApplicationContext context = app.run(args);
    if (args.length > 0) {
      Commander commander = context.getBeansOfType(Commander.class).values().iterator().next();
      commander.processArgs(args);
    }
  }

}
