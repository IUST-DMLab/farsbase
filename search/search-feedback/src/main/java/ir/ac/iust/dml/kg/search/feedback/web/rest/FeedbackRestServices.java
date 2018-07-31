package ir.ac.iust.dml.kg.search.feedback.web.rest;

import io.swagger.annotations.Api;
import ir.ac.iust.dml.kg.search.feedback.access.entities.FeedbackPost;
import ir.ac.iust.dml.kg.search.feedback.logic.FeedbackLogic;
import ir.ac.iust.dml.kg.search.feedback.web.data.FeedbackData;
import ir.ac.iust.dml.kg.search.feedback.web.data.FeedbackEditData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/v1/feedback/")
@Api(tags = "feedback", description = "سرویس‌های بازخورد")
public class FeedbackRestServices {

  @Autowired
  FeedbackLogic logic;

  @RequestMapping(value = "/public/send", method = RequestMethod.POST)
  @ResponseBody
  public boolean send(@RequestBody @Validated FeedbackData data) throws Exception {
    return logic.send(data) != null;
  }

  @RequestMapping(value = "/auth/edit", method = RequestMethod.POST)
  @ResponseBody
  public boolean edit(@RequestBody @Validated FeedbackEditData data) throws Exception {
    return logic.edit(data);
  }

  @RequestMapping(value = "/auth/search", method = RequestMethod.GET)
  @ResponseBody
  public Page<FeedbackPost> search(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int pageSize,
                                   @RequestParam(required = false) String textKeyword,
                                   @RequestParam(required = false) String queryKeyword,
                                   @RequestParam(required = false) Long minSendDate,
                                   @RequestParam(required = false) Long maxSendDate,
                                   @RequestParam(required = false) Boolean approved,
                                   @RequestParam(required = false) Boolean done) throws Exception {
    return logic.search(page, pageSize, textKeyword, queryKeyword, minSendDate, maxSendDate, approved, done);
  }
}
