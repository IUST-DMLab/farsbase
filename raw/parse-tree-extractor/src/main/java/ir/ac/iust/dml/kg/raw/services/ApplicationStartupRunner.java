/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.services;

import ir.ac.iust.dml.kg.raw.services.tree.NewsLogic;
import ir.ac.iust.dml.kg.raw.services.tree.ParsingLogic;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
class ApplicationStartupRunner implements CommandLineRunner {

  private final Log logger = LogFactory.getLog(getClass());
  @Autowired
  private ParsingLogic parsingLogic;

  @Autowired
  private NewsLogic newsLogic;


  @Override
  public void run(String... args) throws Exception {
    logger.info("ApplicationStartupRunner run method Started !!");
    if (args.length == 0) return;
    if (args[0].equals("write")) parsingLogic.writeParses();
    if (args[0].equals("sizes")) parsingLogic.writeSizes();
    if (args[0].equals("patterns")) parsingLogic.writePatterns(false);
    if (args[0].equals("patternsReset")) parsingLogic.writePatterns(true);
    if (args[0].equals("extractFromDb")) parsingLogic.extractFromDb();
    if (args[0].equals("extract")) parsingLogic.extractFromText();
    if (args[0].equals("newsToDb")) newsLogic.writeToDatabase();
  }
}
