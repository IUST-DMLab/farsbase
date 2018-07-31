package ir.ac.iust.dml.kg.evaluation.services.web.rest;

import io.swagger.annotations.Api;
import ir.ac.iust.dml.kg.evaluation.model.KnowledgeGraphResponse;
import ir.ac.iust.dml.kg.evaluation.model.Query;
import ir.ac.iust.dml.kg.evaluation.model.UserResponse;
import ir.ac.iust.dml.kg.evaluation.service.KnowledgeGraphEvaluator;
import ir.ac.iust.dml.kg.evaluation.service.QueryService;
import ir.ac.iust.dml.kg.evaluation.service.UserResponseService;
import ir.ac.iust.dml.kg.evaluation.services.web.rest.factory.EvaluationServicesFactory;
import ir.ac.iust.dml.kg.evaluation.services.web.rest.model.QueryResult;
import ir.ac.iust.dml.kg.evaluation.services.web.rest.model.SimpleSearchResult;
import ir.ac.iust.dml.kg.evaluation.services.web.rest.service.SearchRestService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/v1/evaluation/admin")
@Api(tags = "evaluation", description = "سرویس‌های ارزیابی")
public class EvaluationAdminRestServices {

    private QueryService queryService;
    private UserResponseService userResponseService;
    private KnowledgeGraphEvaluator knowledgeGraphEvaluator;
    private final SearchRestService searchRestService;

    private final EvaluationServicesFactory evaluationServicesFactory;

    @Autowired
    public EvaluationAdminRestServices(SearchRestService searchRestService, EvaluationServicesFactory evaluationServicesFactory) {
        this.searchRestService = searchRestService;
        this.evaluationServicesFactory = evaluationServicesFactory;
    }

    @PostConstruct
    private void init() {
        this.userResponseService = evaluationServicesFactory.getUserResponseService();
        this.queryService = evaluationServicesFactory.getQueryService();
        this.knowledgeGraphEvaluator = evaluationServicesFactory.getKnowledgeGraphEvaluator();
    }

    @RequestMapping(value = "/eval-p-at-k", method = RequestMethod.GET)
    @ResponseBody
    public float evaluatePrecisionAtK(@RequestParam int k) throws Exception {
        List<Query> queriesToEvaluate = this.queryService.getAllQueries();
        List<KnowledgeGraphResponse> KnowledgeGraphResponseList = new ArrayList<>();
        for (Query query : queriesToEvaluate) {
            List<SimpleSearchResult> searchResults = searchRestService.search(query.getQ());
            KnowledgeGraphResponse knowledgeGraphResponse = new KnowledgeGraphResponse();
            knowledgeGraphResponse.setQuery(query.getQ());
            for (SimpleSearchResult searchResult : searchResults) {
                String title = searchResult.getTitle();
                knowledgeGraphResponse.getUriList().add(title);
            }
            KnowledgeGraphResponseList.add(knowledgeGraphResponse);
        }
        float p = knowledgeGraphEvaluator.calculatePrecisionAtK(KnowledgeGraphResponseList, k);

        return p;
    }

    @RequestMapping(value = "/allqueries", method = RequestMethod.GET)
    @ResponseBody
    public List<Query> getAllQueries() throws Exception {
        return queryService.getAllQueries();
    }

    @RequestMapping(value = "/addquery", method = RequestMethod.POST)
    @ResponseBody
    public void addQuery(@RequestBody Query query) throws Exception {
        this.queryService.saveQuery(query);
    }

    @RequestMapping(value = "/deletequery", method = RequestMethod.POST)
    @ResponseBody
    public void deleteQuery(@RequestBody Query query) throws Exception {
        this.queryService.deleteQuery(query);
    }

}
