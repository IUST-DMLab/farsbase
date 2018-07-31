package ir.ac.iust.dml.kg.search.feedback.access.repositories;

import ir.ac.iust.dml.kg.search.feedback.access.entities.FeedbackPost;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface FeedbackPostRepository
    extends MongoRepository<FeedbackPost, ObjectId>, FeedbackPostRepositoryCustom {
  @Query("{ email: {$eq : ?0} }")
  FeedbackPost findByEmail(String email);
}
