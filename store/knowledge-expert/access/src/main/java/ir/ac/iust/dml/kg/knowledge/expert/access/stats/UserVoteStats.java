package ir.ac.iust.dml.kg.knowledge.expert.access.stats;

import ir.ac.iust.dml.kg.knowledge.expert.access.entities.User;
import ir.ac.iust.dml.kg.knowledge.store.client.Vote;
import org.springframework.data.mongodb.core.mapping.DBRef;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Created by HosseiN on 05/06/2017.
 */
public class UserVoteStats {
    @DBRef
    private User user;

    private Vote vote;

    private int count;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Vote getVote() {
        return vote;
    }

    public void setVote(Vote vote) {
        this.vote = vote;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
