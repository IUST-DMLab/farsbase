/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.services.web.rest;

import io.swagger.annotations.Api;
import ir.ac.iust.dml.kg.raw.extractor.ResolvedEntityToken;
import ir.ac.iust.dml.kg.raw.services.access.entities.Article;
import ir.ac.iust.dml.kg.raw.services.access.entities.DependencyPattern;
import ir.ac.iust.dml.kg.raw.services.access.entities.Occurrence;
import ir.ac.iust.dml.kg.raw.services.logic.TextRepositoryLogic;
import ir.ac.iust.dml.kg.raw.services.logic.data.SentenceSelection;
import ir.ac.iust.dml.kg.raw.services.logic.data.TextRepositoryFile;
import ir.ac.iust.dml.kg.raw.services.web.rest.data.RepositoryStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/rest/v1/raw/repository")
@Api(tags = "repository", description = "سرویس‌های گنجینه متن خام")
public class TextRepositoryServices {

  @Autowired
  private TextRepositoryLogic logic;

  @RequestMapping(value = "/ls", method = RequestMethod.GET)
  @ResponseBody
  public List<TextRepositoryFile> ls(@RequestParam(required = false) String path) throws IOException {
    return logic.ls(path);
  }

  @RequestMapping(value = "/get", method = RequestMethod.GET)
  @ResponseBody
  public List<List<ResolvedEntityToken>> get(@RequestParam(required = false) String path) {
    return logic.get(path);
  }

  @RequestMapping(value = "/mark", method = RequestMethod.GET)
  @ResponseBody
  public boolean mark(HttpServletRequest request,
                      @RequestParam(required = false) String path) throws Exception {
    return logic.mark(RawTextRestServices.user(request), path);
  }

  @RequestMapping(value = "/stats", method = RequestMethod.GET)
  @ResponseBody
  public RepositoryStats stats() throws Exception {
    return logic.stats();
  }

  @RequestMapping(value = "/searchArticles", method = RequestMethod.GET)
  @ResponseBody
  public Page<Article> searchArticles(@RequestParam(required = false, defaultValue = "0") int page,
                                      @RequestParam int pageSize,
                                      @RequestParam(required = false) String path,
                                      @RequestParam(required = false) String title,
                                      @RequestParam(required = false) Integer minPercentOfRelations,
                                      @RequestParam(required = false) Boolean approved,
                                      @RequestParam(required = false) String slectedByUsername) {
    return logic.searchArticles(page, pageSize, path, title, minPercentOfRelations, approved, slectedByUsername);
  }

  @RequestMapping(value = "/saveArticle", method = RequestMethod.POST)
  @ResponseBody
  public Article saveArticle(HttpServletRequest request,
                             @RequestBody Article article) throws Exception {
    return logic.saveArticle(RawTextRestServices.user(request), article);
  }

  @RequestMapping(value = "/selectForDependencyRelation", method = RequestMethod.POST)
  @ResponseBody
  public DependencyPattern selectForDependencyRelation(HttpServletRequest request,
                                                       @RequestBody SentenceSelection selection) throws Exception {
    return logic.selectForDependencyRelation(RawTextRestServices.user(request), selection);
  }

  @RequestMapping(value = "/selectForOccurrence", method = RequestMethod.POST)
  @ResponseBody
  public Occurrence selectForOccurence(HttpServletRequest request,
                                       @RequestBody SentenceSelection selection) throws Exception {
    return logic.selectForOccurrence(RawTextRestServices.user(request), selection);
  }
}
