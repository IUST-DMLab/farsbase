/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.services.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class WelcomeController {
  @RequestMapping({"/", ""})
  public String index() {
    return "Greetings from Raw Text Services! " +
        "<br/>" +
        "Select one of these options:" +
        "<br/>" +
        "<a href='/swagger-ui.html'>All public services</a>";
  }
}
