package ir.ac.iust.dml.kg.search.feedback.access.repositories;

import ir.ac.iust.dml.kg.search.feedback.access.entities.FeedbackPost;
import org.springframework.data.domain.Page;

public interface FeedbackPostRepositoryCustom {

  Page<FeedbackPost> search(int page, int pageSize, String textKeyword, String queryKeyword,
                            Long minSendDate, Long maxSendDate, Boolean approved, Boolean done);
}
