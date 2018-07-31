/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.services.web.rest;

import edu.stanford.nlp.pipeline.Annotation;
import io.swagger.annotations.Api;
import ir.ac.iust.dml.kg.raw.SentenceTokenizer;
import ir.ac.iust.dml.kg.raw.TextProcess;
import ir.ac.iust.dml.kg.raw.extractor.ResolvedEntityToken;
import ir.ac.iust.dml.kg.raw.rulebased.ExtractTriple;
import ir.ac.iust.dml.kg.raw.rulebased.RuleAndPredicate;
import ir.ac.iust.dml.kg.raw.rulebased.RuleBasedTripleExtractor;
import ir.ac.iust.dml.kg.raw.services.access.entities.DependencyPattern;
import ir.ac.iust.dml.kg.raw.services.access.entities.Occurrence;
import ir.ac.iust.dml.kg.raw.services.access.entities.Rule;
import ir.ac.iust.dml.kg.raw.services.access.entities.User;
import ir.ac.iust.dml.kg.raw.services.access.repositories.RuleRepository;
import ir.ac.iust.dml.kg.raw.services.logic.FKGfyLogic;
import ir.ac.iust.dml.kg.raw.services.logic.OccurrenceLogic;
import ir.ac.iust.dml.kg.raw.services.logic.UserLogic;
import ir.ac.iust.dml.kg.raw.services.logic.data.AssigneeData;
import ir.ac.iust.dml.kg.raw.services.logic.data.OccurrenceSearchResult;
import ir.ac.iust.dml.kg.raw.services.logic.data.PredicateData;
import ir.ac.iust.dml.kg.raw.services.tree.ParsedWord;
import ir.ac.iust.dml.kg.raw.services.tree.ParsingLogic;
import ir.ac.iust.dml.kg.raw.services.web.rest.data.RuleTestData;
import ir.ac.iust.dml.kg.raw.services.web.rest.data.TextBucket;
import ir.ac.iust.dml.kg.raw.triple.RawTriple;
import kotlin.Pair;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/rest/v1/raw/")
@Api(tags = "raw", description = "سرویس‌های متن خام")
public class RawTextRestServices {

  private static final Logger logger = LoggerFactory.getLogger(RawTextRestServices.class);
  @Autowired
  private OccurrenceLogic occurrenceLogic;
  @Autowired
  private UserLogic userLogic;
  @Autowired
  private ParsingLogic parsingLogic;
  @Autowired
  private FKGfyLogic fkGfyLogic;
  @Autowired
  private RuleRepository ruleDao;

  private final TextProcess tp = new TextProcess();

  @RequestMapping(value = "/FKGfy", method = RequestMethod.POST)
  @ResponseBody
  public List<List<ResolvedEntityToken>> FKGfy(@RequestBody TextBucket data) throws Exception {
    return fkGfyLogic.fkgFy(data.getText());
  }

  @RequestMapping(value = "/related", method = RequestMethod.GET)
  @ResponseBody
  public List<String> related(@RequestParam String uri) {
    return fkGfyLogic.related(uri);
  }

  @RequestMapping(value = "/weightedRelated", method = RequestMethod.GET)
  @ResponseBody
  public List<Pair<String, Integer>> weightedRelated(@RequestParam String uri) {
    return fkGfyLogic.weightedRelated(uri);
  }

  @RequestMapping(value = "/extractTriples", method = RequestMethod.POST)
  @ResponseBody
  public List<RawTriple> extractAll(@RequestBody TextBucket data) throws Exception {
    return fkGfyLogic.extract(data.getText());
  }

  static String user(HttpServletRequest request) throws Exception {
    String proxyUsername = request.getHeader("x-auth-username");
    if (proxyUsername != null && !proxyUsername.isEmpty()) {
      return proxyUsername;
    } else throw new Exception("no user!");
  }

  @RequestMapping(value = "/searchPattern", method = RequestMethod.GET)
  @ResponseBody
  public Page<DependencyPattern> searchPattern(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int pageSize,
      @RequestParam(required = false) Integer maxSentenceLength,
      @RequestParam(required = false, defaultValue = "50") Integer reduceSize,
      @RequestParam(required = false) Integer minSize,
      @RequestParam(required = false) Boolean approved) throws Exception {
    return parsingLogic.searchPattern(page, pageSize, reduceSize, maxSentenceLength, minSize, approved);
  }

  @RequestMapping(value = "/savePattern", method = RequestMethod.POST)
  @ResponseBody
  public DependencyPattern savePattern(@RequestBody DependencyPattern data) throws Exception {
    if (data == null) return null;
    return parsingLogic.save(data);
  }

  @RequestMapping(value = "/dependencyParsePost", method = RequestMethod.POST)
  @ResponseBody
  public List<ParsedWord> dependencyParsePost(@RequestBody TextBucket textBucket) throws Exception {
    if (textBucket.getText() == null) return null;
    return parsingLogic.dependencySentence(textBucket.getText());
  }

  @RequestMapping(value = "/dependencyParseGet", method = RequestMethod.GET)
  @ResponseBody
  public List<ParsedWord> dependencyParseGet(@RequestParam String text) throws Exception {
    return parsingLogic.dependencySentence(text);
  }

  @RequestMapping(value = "/predictByPatternPost", method = RequestMethod.POST)
  @ResponseBody
  public List<RawTriple> predictByPatternPost(@RequestBody TextBucket textBucket) throws Exception {
    if (textBucket.getText() == null) return null;
    return parsingLogic.extract("http://dmls.iust.ac.ir/raw/", (new Date()).toString(), textBucket.getText());
  }

  @RequestMapping(value = "/predictByPatternGet", method = RequestMethod.GET)
  @ResponseBody
  public List<RawTriple> predictByPatternGet(@RequestParam String text) throws Exception {
    return parsingLogic.extract("http://dmls.iust.ac.ir/raw/", (new Date()).toString(), text);
  }

  @RequestMapping(value = "/approve", method = RequestMethod.GET)
  @ResponseBody
  public Occurrence edit(@RequestParam String id,
                         @RequestParam(required = false) Boolean approved) throws Exception {
    final Occurrence e = occurrenceLogic.findOne(id);
    if (e == null) return null;
    e.setApproved(approved);
    occurrenceLogic.save(e);
    return e;
  }

  @RequestMapping(value = "/export", method = RequestMethod.GET)
  @ResponseBody
  public List<Occurrence> export() throws Exception {
    return occurrenceLogic.export().getContent();
  }

  @RequestMapping(value = "/search", method = RequestMethod.GET)
  @ResponseBody
  public OccurrenceSearchResult search(
      HttpServletRequest request,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int pageSize,
      @RequestParam(required = false) String predicate,
      @RequestParam(required = false, defaultValue = "true") boolean like,
      @RequestParam(required = false) Integer minOccurrence,
      @RequestParam(required = false) Boolean approved,
      @RequestParam(required = false) String assigneeUsername
  ) throws Exception {
    final String user = user(request);
    logger.info("current user is " + user);
    if (!user.equals("superuser") && assigneeUsername == null) assigneeUsername = user;
    logger.info("assigned user is " + assigneeUsername);
    return occurrenceLogic.search(user, page, pageSize, predicate, like, minOccurrence, approved, assigneeUsername);
  }

  @RequestMapping(value = "/predicates", method = RequestMethod.GET)
  @ResponseBody
  public Page<PredicateData> predicates(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int pageSize,
      @RequestParam(required = false) String predicate,
      @RequestParam(required = false, defaultValue = "false") boolean fillAssignees
  ) throws Exception {
    return occurrenceLogic.predicates(page, pageSize, predicate, fillAssignees);
  }

  @RequestMapping(value = "/assigneeCount", method = RequestMethod.GET)
  @ResponseBody
  public List<AssigneeData> assigneeCount(
      @RequestParam(required = false) String predicate
  ) throws Exception {
    return occurrenceLogic.assigneeCount(predicate);
  }

  @RequestMapping(value = "/listUsers", method = RequestMethod.GET)
  @ResponseBody
  public List<User> listUsers() throws Exception {
    return userLogic.findAll();
  }

  @RequestMapping(value = "/assign", method = RequestMethod.GET)
  @ResponseBody
  public int assign(
      HttpServletRequest request,
      @RequestParam String username,
      @RequestParam(required = false) String predicate,
      @RequestParam int count
  ) throws Exception {
    if (username == null) username = user(request);
    return userLogic.assign(username, predicate, count);
  }

  @RequestMapping(value = "/rules", method = RequestMethod.GET)
  @ResponseBody
  public Page<Rule> rules(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int pageSize) throws Exception {
    return ruleDao.findAll(new PageRequest(page, pageSize));
  }

  @Autowired
  RuleBasedTripleExtractor ruleBasedTripleExtractor;

  @RequestMapping(value = "/editRule", method = RequestMethod.GET)
  @ResponseBody
  public Rule editRule(
      @RequestParam(required = false) String id,
      @RequestParam String rule,
      @RequestParam String predicate,
      @RequestParam boolean approved) throws Exception {
    Rule e = (id == null) ? null : ruleDao.findOne(new ObjectId(id));
    if (e == null) e = new Rule();
    e.setRule(rule);
    e.setPredicate(predicate);
    e.setApproved(approved);
    ruleDao.save(e);
    try {
      ruleBasedTripleExtractor.init();
    } catch (Throwable ignored) {
    }
    return e;
  }

  @RequestMapping(value = "/removeRule", method = RequestMethod.GET)
  @ResponseBody
  public Rule removeRule(@RequestParam String id) throws Exception {
    final Rule e = ruleDao.findOne(new ObjectId(id));
    if (e == null) return null;
    ruleDao.delete(e);
    ruleBasedTripleExtractor.init();
    return e;
  }

  @RequestMapping(value = "/extractTripleFromText", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
  @ResponseBody
  public List<RawTriple> extractTriplesByRules(@RequestBody TextBucket data) throws Exception {
    List<RawTriple> result = new ArrayList<>();
    RuleTestData ruleTestData = new RuleTestData();
    ruleTestData.setText(data.getText());
    List<Rule> rules = ruleDao.findAll();
    List<RuleAndPredicate> ruleAndPredicates = new ArrayList<>();
    for (Rule rule : rules) {
      RuleAndPredicate ruleAndPredicate = new RuleAndPredicate();
      ruleAndPredicate.setRule(rule.getRule());
      ruleAndPredicate.setPredicate(rule.getPredicate());
      ruleAndPredicates.add(ruleAndPredicate);
    }

    ruleTestData.setRules(ruleAndPredicates);
    result = ruleTest(ruleTestData);
    return result;
  }


  @RequestMapping(value = "/ruleTest", method = RequestMethod.POST)
  @ResponseBody
  public List<RawTriple> ruleTest(@RequestBody RuleTestData data) throws Exception {
    List<RawTriple> result = new ArrayList<>();
    ExtractTriple extractTriple = new ExtractTriple(data.getRules());
    final List<String> lines = SentenceTokenizer.SentenceSplitterRaw(data.getText());
    for (String line : lines) {
      Annotation annotation = new Annotation(line);
      tp.preProcess(annotation);
      result.addAll(extractTriple.extractTripleFromAnnotation(annotation));
    }
    return result;
  }
}
