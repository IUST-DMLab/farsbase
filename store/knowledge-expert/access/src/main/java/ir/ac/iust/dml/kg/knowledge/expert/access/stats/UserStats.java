package ir.ac.iust.dml.kg.knowledge.expert.access.stats;

import ir.ac.iust.dml.kg.knowledge.expert.access.entities.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Created by HosseiN on 05/06/2017.
 */
public class UserStats {
    @Id
    @DBRef
    private User user;

    private int count;


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
