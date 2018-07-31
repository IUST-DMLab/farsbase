package ir.ac.iust.dml.kg.search.feedback.logic;

import ir.ac.iust.dml.kg.search.feedback.access.entities.FeedbackPost;
import ir.ac.iust.dml.kg.search.feedback.access.repositories.FeedbackPostRepository;
import ir.ac.iust.dml.kg.search.feedback.web.data.FeedbackData;
import ir.ac.iust.dml.kg.search.feedback.web.data.FeedbackEditData;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class FeedbackLogic {

  @Autowired
  FeedbackPostRepository repository;

  public FeedbackPost send(FeedbackData data) {
    final FeedbackPost post = new FeedbackPost();
    post.setName(data.getName());
    post.setEmail(data.getEmail());
    post.setSendTime(System.currentTimeMillis());
    post.setText(data.getText());
    post.setQuery(data.getQuery());
    post.setApproved(false);
    post.setDone(false);
    return repository.save(post);
  }

  public boolean edit(FeedbackEditData data) {
    final FeedbackPost post = repository.findOne(new ObjectId(data.getUid()));
    if (post == null) return false;
    post.setNote(data.getNote());
    post.setApproved(data.getApproved());
    post.setDone(data.getDone());
    repository.save(post);
    return true;
  }

  public Page<FeedbackPost> search(int page, int pageSize, String textKeyword, String queryKeyword,
                                   Long minSendDate, Long maxSendDate, Boolean approved, Boolean done) {
    return repository.search(page, pageSize, textKeyword, queryKeyword, minSendDate, maxSendDate, approved, done);
  }
}
