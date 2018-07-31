package ir.ac.iust.dml.kg.evaluation.services.web.rest;

import io.swagger.annotations.Api;
import ir.ac.iust.dml.kg.evaluation.model.Query;
import ir.ac.iust.dml.kg.evaluation.model.UserResponse;
import ir.ac.iust.dml.kg.evaluation.service.QueryService;
import ir.ac.iust.dml.kg.evaluation.service.UserResponseService;
import ir.ac.iust.dml.kg.evaluation.services.web.rest.factory.EvaluationServicesFactory;
import ir.ac.iust.dml.kg.evaluation.services.web.rest.model.QueryResult;
import ir.ac.iust.dml.kg.evaluation.services.web.rest.model.SimpleSearchResult;
import ir.ac.iust.dml.kg.evaluation.services.web.rest.service.SearchRestService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/v1/evaluation/")
@Api(tags = "evaluation", description = "سرویس‌های ارزیابی")
public class EntityEvaluationRestServices {

    private QueryService queryService;
    private UserResponseService userResponseService;
    private final SearchRestService searchRestService;

    private final EvaluationServicesFactory evaluationServicesFactory;

    @Autowired
    public EntityEvaluationRestServices(SearchRestService searchRestService, EvaluationServicesFactory evaluationServicesFactory) {
        this.searchRestService = searchRestService;
        this.evaluationServicesFactory = evaluationServicesFactory;
    }

    @PostConstruct
    private void init() {
        this.userResponseService = evaluationServicesFactory.getUserResponseService();
        this.queryService = evaluationServicesFactory.getQueryService();
    }

    @RequestMapping(value = "/next", method = RequestMethod.GET)
    @ResponseBody
    public QueryResult getUnreadQueryResult(@RequestParam(required = false) String user, HttpServletRequest request) throws Exception {
        if (user == null || user.isEmpty()) {
            String proxyUserId = request.getHeader("x-auth-identifier");
            if (proxyUserId != null && !proxyUserId.isEmpty()) {
                user = proxyUserId;
            }
        }
        Query query = queryService.getUnreadQueryByPersonId(user);
        List<SimpleSearchResult> searchResults = searchRestService.search(query.getQ());
        if (query != null) {
            QueryResult queryResult = new QueryResult();
            queryResult.setQuery(query);
            queryResult.setSearchResults(searchResults);
            return queryResult;
        }

        return null;
    }

    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    @ResponseBody
    public boolean submit(@RequestParam(required = false) String user, @RequestBody UserResponse userResponse, HttpServletRequest request) throws Exception {
        if (userResponse != null) {
            if (user == null || user.isEmpty()) {
                String proxyUserId = request.getHeader("x-auth-identifier");
                if (proxyUserId != null && !proxyUserId.isEmpty()) {
                    user = proxyUserId;
                }
            }
            if (user != null) {
                userResponse.setPersonId(user);
            }

            userResponseService.saveUserResponse(userResponse);
            return true;
        } else {
            return false;
        }
    }

    private QueryResult getFakeQueryResult() {
        QueryResult queryResult = new QueryResult();
        Query query = new Query();
        query.setQ("بازیگران دورهمی");
        queryResult.setQuery(query);

        SimpleSearchResult result1 = new SimpleSearchResult("https://fa.wikipedia.org/wiki/%D9%85%D9%87%D8%B1%D8%A7%D9%86_%D9%85%D8%AF%DB%8C%D8%B1%DB%8C", "مهران_مدیری");
        SimpleSearchResult result2 = new SimpleSearchResult("https://fa.wikipedia.org/wiki/%D8%B3%DB%8C%D8%A7%D9%85%DA%A9_%D8%A7%D9%86%D8%B5%D8%A7%D8%B1%DB%8C", "سیامک_انصاری");
        SimpleSearchResult result3 = new SimpleSearchResult("https://fa.wikipedia.org/wiki/%D8%B1%D8%A7%D9%85%D8%A8%D8%AF_%D8%AC%D9%88%D8%A7%D9%86", "رامبد_جوان");

        List<SimpleSearchResult> results = new ArrayList<>();
        results.add(result1);
        results.add(result2);
        results.add(result3);
        queryResult.setSearchResults(results);
        return queryResult;

    }
}
