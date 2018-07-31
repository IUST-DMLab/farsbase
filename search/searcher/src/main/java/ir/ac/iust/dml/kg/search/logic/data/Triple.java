package ir.ac.iust.dml.kg.search.logic.data;

import java.io.Serializable;

public class Triple implements Serializable {
    private String subject;
    private String predicate;
    private String object;

    public Triple(String subject, String predicate, String object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    public String getSubject() {
        return subject;
    }

    public String getPredicate() {
        return predicate;
    }

    public String getObject() {
        return object;
    }
}
