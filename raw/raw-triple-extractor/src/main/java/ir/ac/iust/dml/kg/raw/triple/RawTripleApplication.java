/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.triple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "ir.ac.iust.dml.kg.raw")
@EnableScheduling
public class RawTripleApplication {
  public static void main(String[] args) {
    SpringApplication.run(RawTripleApplication.class, args);
  }
}
