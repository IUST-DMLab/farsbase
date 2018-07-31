package ir.ac.iust.dml.kg.search.services.web.rest;

import io.swagger.annotations.Api;
import ir.ac.iust.dml.kg.search.logic.Searcher;
import ir.ac.iust.dml.kg.search.logic.data.SearchResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping("/rest/v1/searcher/")
@Api(tags = "searcer", description = "سرویس‌های جستجو")
public class EntitySearchRestServices {

    final private Searcher searcher = Searcher.getInstance();

  public EntitySearchRestServices() throws Exception {
  }

  @RequestMapping(value = "/search", method = RequestMethod.GET)
  @ResponseBody
  public SearchResult search(HttpServletRequest request, @RequestParam(required = false) String keyword) throws Exception {
    System.out.println((new Date()) + "\t request:search\t IP:" + request.getRemoteHost() + "\t Query:" + keyword);
    return searcher.search(keyword);
  }

}
